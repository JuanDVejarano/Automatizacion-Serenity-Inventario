@clientes
Feature: HU-10 Registrar cliente
  Como usuario con rol Ventas o Administrador
  Quiero registrar un nuevo cliente en el sistema
  Para asociarlo a las ventas realizadas en la organizacion

  Background:
    Given el usuario ha iniciado sesion en el modulo de clientes

  Scenario: Registro exitoso de cliente
    When el usuario registra un cliente con todos los campos obligatorios
    Then el sistema muestra el mensaje de exito de cliente "Cliente registrado exitosamente"

  Scenario: Registro con telefono secundario
    When el usuario registra un cliente incluyendo el telefono secundario
    Then el sistema muestra el mensaje de exito de cliente "Cliente registrado exitosamente"

  Scenario: Cedula o NIT duplicado
    Given el usuario registra previamente un cliente en el sistema
    When el usuario intenta registrar otro cliente con la misma cedula o NIT
    Then el sistema muestra el mensaje de error de cliente "Ya existe un cliente con esa cedula"

  Scenario: Correo duplicado en clientes
    Given el usuario registra previamente un cliente en el sistema
    When el usuario intenta registrar otro cliente con el mismo correo
    Then el sistema muestra el mensaje de error de cliente "Ya existe un cliente con ese correo"

  Scenario: Campos obligatorios vacios en clientes
    When el usuario abre el formulario de registro de cliente
    And el usuario intenta registrar el cliente sin diligenciar los campos
    Then el sistema muestra el mensaje de error de cliente "Todos los campos obligatorios deben ser diligenciados"

  Scenario: Acceso no autorizado al modulo de clientes
    Given el usuario inicia sesion con el rol "Produccion"
    When el usuario intenta acceder al modulo de clientes
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
