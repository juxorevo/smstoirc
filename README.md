# Cette application sert à envoyer et recevoir vos SMS depuis ordinateur. 
 
 Les avantages sont multiples :
 - Cela permet pour les personnes moins alaises avec les claviers des téléphones portables d'avoir un clavier physique.

- Quand vous êtes au travail, cela permet d'avoir une zone de conversation confortable. On évite d'être sur son portable plus ou moins régulièrement au cours de la journée. Nous sommes donc plus discret sur nos communications.

Une page internet vous permet d'accéder à vos conversations. 

* Pour utiliser cette application vous connecter sur ce lien : https://irc.41m4rd.ovh/, mettez un pseudo et connectez vous.

* Télécharger l'application, mettez le même pseudo que vous avec indiqué sur la page et appuyez sur < Start >.

Vous pouvez vous envoyez un texto à vous même pour tester le fonctionnement de l'application.

----------------------------------------------------------------------------------------------------------------------------------------------
# Pour les développeur : 

L'application passe par un serveur IRC, l'interface web fournie et l'application sont pré-configurées pour un serveur IRC, mais vous pouvez utiliser votre propre serveur y compris sur l'interface WEB.

# Le processus : 

L'application reçoit un SMS démarre un thread applicatif qui se connecte au serveur IRC avec un pseudo aléatoire pour ne pas corrompre l'identité de vos contacts. Elle envoie en suite un SMS en conversation privé vers le pseudo que vous avez définit

L'application analyse ensuite les messages qu'elle reçoit sur cette connexion spécifique. Si elle reçoit un message elle le transfert tout simplement par SMS au numéro qui a initialisé la conversation.

Application codé en API 26 retro compatible jusqu'à api 15 à priori.

----------------------------------------------------------------------------------------------------------------------------------------------
# Information IMPORTANTE /!\ : 

- Vous devez d'abord vous connecter à IRC pour vérifier que le pseudo que vous allez utiliser est libre.
- Vous devez enregistrer votre pseudo avec authentification pour éviter de vous faire usurper.
- L'application est en bêta nous essayons de renforcer sa sécurité.
- Nous mettons en place des certificats SSL entre toutes les connexions.
- L'application utilise une connexion SSL pour se connecter au SERVEUR IRC.

    L'application nécessite :
- L'accès à une connexion à internet.
- L'accès à votre répertoire, pour déterminer le nom du contact qui vous envoie le message.
- L'accès à vos SMS.

-----------------------------------------------------------------------------------------------------------------------------------------------
# Bug identifié : 

- On ne reçoit pas les messages d'un contact qui n'est pas dans notre liste de contact (numéro de téléphone).

-----------------------------------------------------------------------------------------------------------------------------------------------
# Git HUB

https://github.com/juxorevo/smstoirc
