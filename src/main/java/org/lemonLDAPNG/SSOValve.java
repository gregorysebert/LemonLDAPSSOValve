package org.lemonLDAPNG;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.catalina.valves.ValveBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class SSOValve extends ValveBase
{
    private static Log log = LogFactory.getLog(SSOValve.class);
    private static final String info = "org.lemonLDAPNG.SSOValve/1.0";
    private String userKey = null;

    private String roleKey = null;

    private String roleSeparator = null;

    boolean flagAllows = false;

    private Pattern[] allows = { Pattern.compile("^.*$") };

    private boolean passThrough = false;

    public String getInfo() {
        return "org.lemonLDAPNG.SSOValve/1.0";
    }

    public void invoke(Request request, Response response) throws IOException, ServletException
    {
        HttpServletRequest httpServletRequest = request.getRequest();

        String remoteAdress = request.getRequest().getRemoteAddr();

        for (int j = 0; j < this.allows.length; j++) {
            if (log.isDebugEnabled())
                log.debug("Pattern " + this.allows[j].pattern() + " tested on  ip remote " + remoteAdress);
            if (!this.allows[j].matcher(remoteAdress).matches())
                continue;
            List roles = new ArrayList();

            String user = httpServletRequest.getHeader(this.userKey);
            String role = httpServletRequest.getHeader(this.roleKey);
            if (log.isDebugEnabled()) {
                log.debug("Processing WebSSO request for  " + request.getMethod() + " " + request.getRequestURI());
            }

            if ((user != null) && (role != null) &&
                    (log.isDebugEnabled())) {
                log.debug("Found data User [ " + user + "] with role [ " + role + "]");
            }

            if ((this.roleSeparator != null) && (role != null)) {
                String[] res = role.split(this.roleSeparator);
                for (int i = 0; i < res.length; i++) {
                    roles.add(res[i]);
                }
            }
            else if (role != null) {
                roles.add(role);
            }
            if (user != null) {
                request.setUserPrincipal(new GenericPrincipal(user, "", roles));
            } else if (!this.passThrough) {
                if (log.isDebugEnabled())
                    log.debug("PassThrough disable, send 403 error");
                response.sendError(403);
                return;
            }
            getNext().invoke(request, response);
            return;
        }

        if (this.flagAllows) response.sendError(403);
    }

    protected Pattern[] precalculate(String list)
    {
        if (list == null)
            return new Pattern[0];
        list = list.trim();
        if (list.length() < 1)
            return new Pattern[0];
        list = list + ",";
        ArrayList reList = new ArrayList();

        while (list.length() > 0)
        {
            int comma = list.indexOf(',');
            if (comma < 0)
                break;
            String pattern = list.substring(0, comma).trim();
            try {
                reList.add(Pattern.compile(pattern));
            } catch (PatternSyntaxException e) {
                IllegalArgumentException iae = new IllegalArgumentException(sm.getString("requestFilterValve.syntax", new Object[] { pattern }));

                throw iae;
            }
            list = list.substring(comma + 1);
        }
        Pattern[] reArray = new Pattern[reList.size()];
        return (Pattern[])(Pattern[])reList.toArray(reArray);
    }

    public String getUserKey() {
        return this.userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
        if ((log.isDebugEnabled()) && (userKey != null))
            log.debug("UserKey [" + this.userKey + "]");
    }

    public String getRoleKey() {
        return this.roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
        if ((log.isDebugEnabled()) && (roleKey != null))
            log.debug("RoleKey [" + this.roleKey + "]");
    }

    public String getRoleSeparator() {
        return this.roleSeparator;
    }

    public void setRoleSeparator(String roleSeparator) {
        this.roleSeparator = roleSeparator;
        if ((log.isDebugEnabled()) && (roleSeparator != null))
            log.debug("RoleSeparator [" + this.roleSeparator + "]");
    }

    public String getAllows() {
        return "";
    }

    public void setAllows(String allows)
    {
        this.allows = precalculate(allows);
        this.flagAllows = true;
    }

    public String getPassThrough() {
        return String.valueOf(this.passThrough);
    }

    public void setPassThrough(String passThrough) {
        this.passThrough = Boolean.valueOf(passThrough).booleanValue();
        if ((log.isDebugEnabled()) && (passThrough != null))
            log.debug("PassThrough [" + this.passThrough + "]");
    }
}