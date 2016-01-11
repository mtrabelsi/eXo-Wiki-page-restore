# eXo-Wiki-page-restore
Ability to restore wiki pages from the trash: https://community.exoplatform.com/portal/content/addon-detail/wiki-page-restore

# Installation

1- git clone https://github.com/smartytwiti/eXo-Wiki-page-restore.git

2- mvn clean install

3- Deploy the JAR to your server's /lib folder

# How to use

When you deleted a wiki page it will be sent to the **wiki Trash**, it's hidden by default, to show it you need to go under the **Site Explorer/Collaboration drive** then in the right click on **setup your browsing preferences**, a popup will be shown, click on **Advanced** and make sure that the **Show Hidden Nodes** and **Show Non-document Nodes** checkboxes are checked, save the changes.

![Alt text](https://github.com/smartytwiti/eXo-Wiki-page-restore/blob/master/doc/showHidden.png "Show Hidden Nodes")

You can now access to **/exo:applications/eXoWiki/wikis/intranet/Trash**, you'll find all the deleted files there. To restore a page you can simply use the **contextual menu** : right click then **restore the wiki page**.

![Alt text](https://github.com/smartytwiti/eXo-Wiki-page-restore/blob/master/doc/restore.png "Restore wiki Pages")


A message will inform you about the operation result:

![Alt text](https://github.com/smartytwiti/eXo-Wiki-page-restore/blob/master/doc/restored.png "Success")

When another page with the same name already in the wiki. You can't restore that page until you rename or delete the duplicated page:

![Alt text](https://github.com/smartytwiti/eXo-Wiki-page-restore/blob/master/doc/erroHandling.png "Fail")

# Translation
The current version contain **only English language**, to add your own translation you need to create a **.properties** file under the *restore-page-uiextension/src/main/resources/locale/org/exoplatform/restorewikipage* folder. In order to get it recognized, the properties file needs to follow specific naming convention: RestoreWikiPage_**XX**.properties, where the **XX** is the **language code**.
