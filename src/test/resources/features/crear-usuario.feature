@usuarios
Feature: HU-07 Crear usuario del sistema
  Como usuario con rol Recursos Humanos o Administrador
  Quiero crear un usuario en el sistema
  Para permitir el acceso a los empleados segun su rol en la organizacion

        Background:
            Given el administrador ha iniciado sesion para gestion de usuarios

        Scenario: Creacion exitosa de usuario
            Given existe un empleado activo disponible para asignar usuario
             When el usuario diligencia el formulario de creacion de usuario
             Then el sistema muestra el mensaje de exito de usuario "Usuario creado exitosamente"

        Scenario: Empleado con multiples usuarios
            Given existe un empleado activo disponible para asignar usuario
             When el usuario crea un primer usuario para ese empleado con rol "Ventas"
              And el usuario crea un segundo usuario para el mismo empleado con rol "Produccion"
             Then el sistema muestra el mensaje de exito de usuario "Usuario creado exitosamente"

        Scenario: Nombre de usuario duplicado
            Given existe un empleado activo disponible para asignar usuario
              And el usuario crea un usuario de prueba para verificar duplicado
             When el usuario intenta crear otro usuario con el mismo nombre de usuario
             Then el sistema muestra el mensaje de error de usuario "Ya existe un usuario con ese nombre de usuario"

        Scenario: Contrasena invalida
            Given existe un empleado activo disponible para asignar usuario
             When el usuario ingresa todos los campos con una contrasena de 6 caracteres
             Then el sistema muestra el mensaje de error de usuario "La contrasena debe tener mas de 6 caracteres"

        Scenario: Campos obligatorios vacios
             When el usuario navega al formulario de creacion de usuarios
              And el usuario intenta crear un usuario sin diligenciar los campos
             Then el sistema muestra el mensaje de error de usuario "Todos los campos son obligatorios"

        Scenario: Acceso no autorizado al modulo de creacion de usuarios
            Given el usuario inicia sesion con el rol "Ventas"
             When el usuario intenta acceder al modulo de creacion de usuarios
             Then el sistema muestra la pagina de acceso denegado
              And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
