/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acme;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import org.exoplatform.ecm.webui.component.explorer.UIJCRExplorer;
import org.exoplatform.ecm.webui.component.explorer.UIWorkingArea;
import org.exoplatform.ecm.webui.component.explorer.control.filter.HasRemovePermissionFilter;
import org.exoplatform.ecm.webui.component.explorer.control.filter.IsCheckedOutFilter;
import org.exoplatform.ecm.webui.component.explorer.control.filter.IsNotLockedFilter;
import org.exoplatform.ecm.webui.component.explorer.control.listener.UIWorkingAreaActionListener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.resources.ResourceBundleService;
import org.exoplatform.services.wcm.utils.WCMCoreUtils;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIApplication;
import static org.exoplatform.webui.core.UIComponent.OBJECTID;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.ext.filter.UIExtensionFilter;
import org.exoplatform.webui.ext.filter.UIExtensionFilters;
import org.exoplatform.webui.ext.manager.UIAbstractManager;
import org.exoplatform.webui.ext.manager.UIAbstractManagerComponent;

/**
 *
 * @author exo
 */
@ComponentConfig(
        events = {
            @EventConfig(listeners = RestoreWikiManageComponent.RestoreWikiActionListener.class)
        }
)
public class RestoreWikiManageComponent extends UIAbstractManagerComponent {

    private static final List<UIExtensionFilter> FILTERS =
        Arrays.asList(  new UIExtensionFilter[]{
                            new MyUIFilter(),
                            new IsNotLockedFilter(),
                            new IsCheckedOutFilter(),
                            new HasRemovePermissionFilter()
                            });
    private final static Log LOG  = ExoLogger.getLogger(RestoreWikiManageComponent.class.getName());

    @UIExtensionFilters
    public List<UIExtensionFilter> getFilters() {
        return FILTERS;
    }

    @Override
    public Class<? extends UIAbstractManager> getUIAbstractManagerClass() {
        return null;
    }

    public static class RestoreWikiActionListener extends UIWorkingAreaActionListener<RestoreWikiManageComponent> {
        @Override
        public void processEvent(Event<RestoreWikiManageComponent> event) throws Exception {
            UIWorkingArea uiWorkingArea = event.getSource().getParent();
            UIJCRExplorer uiExplorer = uiWorkingArea.getAncestorOfType(UIJCRExplorer.class);
            UIApplication uiApp = uiWorkingArea.getAncestorOfType(UIApplication.class);
            
            String srcPath = event.getRequestContext().getRequestParameter(OBJECTID);
            Matcher matcher = UIWorkingArea.FILE_EXPLORER_URL_SYNTAX.matcher(srcPath);
            ResourceBundleService service = WCMCoreUtils.getService(ResourceBundleService.class);
            ResourceBundle bundle = service.getResourceBundle(service.getSharedResourceBundleNames(),event.getRequestContext().getLocale());        

            String wsName, path;
            Node node;
            if (matcher.find()) {
                wsName = matcher.group(1);
                path = matcher.group(2);
            } else {
                throw new IllegalArgumentException("The ObjectId is invalid '" + srcPath + "'");
            }
            Session session = uiExplorer.getSessionByWorkspace(wsName);
            node = uiExplorer.getNodeByPath(path, session, false);
            String restorePath = node.getProperty("parentPath").getString();

            try {
                //Move the node
                session.move(node.getParent().getPath() + "/" + node.getName(), restorePath + "/" + node.getName());
                session.save();
                //then remove it's mixin
                node.removeMixin("wiki:removed");
                node.save();
                //popup the succeed message
                uiApp.addMessage(new ApplicationMessage(bundle.getString("RestoreWikiPage.response.success"),
                                 null, ApplicationMessage.INFO));
                //Log the succeed message
                LOG.info(bundle.getString("RestoreWikiPage.response.success"));
            } catch (javax.jcr.PathNotFoundException exp) {
                //popup the PathNotFoundException message
                uiApp.addMessage(new ApplicationMessage(bundle.getString("RestoreWikiPage.response.fail.pathNotFound"),
                        null, ApplicationMessage.ERROR));
                //Log the PathNotFoundException message
                LOG.error(bundle.getString("RestoreWikiPage.response.fail.pathNotFound"), exp);
               return;
            } catch (javax.jcr.ItemExistsException exp) {
                //popup the ItemExistsException message
                uiApp.addMessage(new ApplicationMessage(bundle.getString("RestoreWikiPage.response.fail.alreadyExists"),
                        null, ApplicationMessage.WARNING));
                //Log the ItemExistsException message
                LOG.error(bundle.getString("RestoreWikiPage.response.fail.alreadyExists"), exp);
                return;
            } catch (Exception exp) {
                //popup the ItemExistsException message
                uiApp.addMessage(new ApplicationMessage(bundle.getString("RestoreWikiPage.response.fail.unknown"),
                        null, ApplicationMessage.ERROR));
                //Log the ItemExistsException message
                LOG.error(bundle.getString("RestoreWikiPage.response.fail.unknown"), exp);
                return;
            }
        }
    }

}
