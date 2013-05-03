<html>
    <head>
        <title>Error on the server</title>
        <meta name="layout" content="main" />
    </head>
    <body>

      <h1 style="margin-left:20px;color:#006dba;" >
      		Une erreur inattendue s'est produite. L'administrateur de l'application en a été notifié.
                        <br/>
            Veuillez reessayer, ou
              <a href="mailto:henri.martin@gmail.com?
              subject= [${GrailsUtil.environment}] Application Error occured : '${exception?.message?.encodeAsHTML()}'">
              contactez
              </a> l'administrateur de l'application

      </h1>

    </body>
</html>