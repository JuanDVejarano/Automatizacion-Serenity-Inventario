@empleados
Feature: HU-04 Registrar empleado
  Como usuario con rol Recursos Humanos
  Quiero registrar un nuevo empleado en el sistema
  Para mantener el registro del personal de la organizacion

  Background:
    Given el usuario esta autenticado en el modulo de registro de empleados

  Scenario: Registro exitoso de empleado
    When el usuario registra un empleado con todos los campos obligatorios
    Then el sistema muestra el mensaje de exito "Empleado registrado exitosamente"

  Scenario: Registro con telefono secundario
    When el usuario registra un empleado incluyendo el telefono secundario
    Then el sistema muestra el mensaje de exito "Empleado registrado exitosamente"

  Scenario: Cedula duplicada
    Given el usuario registra previamente un empleado en el sistema
    When el usuario intenta registrar otro empleado con la misma cedula
    Then el sistema muestra el mensaje de error del empleado "Ya existe un empleado con esa cedula"

  Scenario: Correo duplicado
    Given el usuario registra previamente un empleado en el sistema
    When el usuario intenta registrar otro empleado con el mismo correo
    Then el sistema muestra el mensaje de error del empleado "Ya existe un empleado con ese correo"

  Scenario: Campos obligatorios vacios
    When el usuario intenta registrar un empleado sin diligenciar los campos obligatorios
    Then el sistema muestra el mensaje de error del empleado "Todos los campos obligatorios deben ser diligenciados"

  Scenario: Acceso no autorizado al modulo de registro de empleados
    Given el usuario inicia sesion con el rol "Ventas"
    When el usuario intenta acceder al modulo de registro de empleados
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
