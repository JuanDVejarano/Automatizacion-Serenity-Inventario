Feature: HU-01 Iniciar sesion en el sistema
  Como usuario del sistema
  Quiero iniciar sesion con mis credenciales
  Para acceder a los modulos segun mi rol asignado

        Background:
            Given el usuario navega a la pagina de login

        Scenario: Inicio de sesion exitoso con credenciales validas
             When el usuario ingresa el nombre de usuario "admin"
              And el usuario ingresa la contrasena "admin1412"
              And el usuario hace clic en el boton iniciar sesion
             Then el sistema redirige al dashboard del usuario

        Scenario: Error al ingresar contrasena de 6 caracteres o menos
             When el usuario ingresa el nombre de usuario "admin"
              And el usuario ingresa la contrasena "admin1"
              And el usuario hace clic en el boton iniciar sesion
             Then el sistema muestra el mensaje de error "La contrasena debe tener mas de 6 caracteres"

        Scenario: Error al ingresar credenciales incorrectas
             When el usuario ingresa el nombre de usuario "usuarioFalso"
              And el usuario ingresa la contrasena "claveIncorrecta"
              And el usuario hace clic en el boton iniciar sesion
             Then el sistema muestra el mensaje de error "Usuario o contrasena incorrectos"

        Scenario: Error al dejar los campos vacios
             When el usuario hace clic en el boton iniciar sesion
             Then el sistema muestra el mensaje de error "Todos los campos son obligatorios"
