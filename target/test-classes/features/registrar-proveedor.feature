@proveedores
Feature: HU-22 Registrar proveedor
  Como usuario con rol Produccion o Administrador
  Quiero registrar un nuevo proveedor en el sistema
  Para mantener el directorio de proveedores de materia prima

  Background:
    Given el usuario ha iniciado sesion en el modulo de proveedores

  Scenario: Registro exitoso de proveedor
    When el usuario registra un proveedor con todos los campos obligatorios
    Then el sistema muestra el mensaje de exito de proveedor "Proveedor registrado exitosamente"

  Scenario: Registro exitoso con campos opcionales
    When el usuario registra un proveedor con todos los campos incluyendo opcionales
    Then el sistema muestra el mensaje de exito de proveedor "Proveedor registrado exitosamente"

  Scenario: NIT duplicado
    Given existe un proveedor registrado en el sistema
    When el usuario intenta registrar un proveedor con el mismo NIT
    Then el sistema muestra el mensaje de error de proveedor "Ya existe un proveedor con ese NIT"

  Scenario: Nombre duplicado
    Given existe un proveedor registrado en el sistema
    When el usuario intenta registrar un proveedor con el mismo nombre
    Then el sistema muestra el mensaje de error de proveedor "Ya existe un proveedor con ese nombre"

  Scenario: Correo duplicado
    Given existe un proveedor registrado en el sistema
    When el usuario intenta registrar un proveedor con el mismo correo
    Then el sistema muestra el mensaje de error de proveedor "Ya existe un proveedor con ese correo"

  Scenario: Campos obligatorios vacios
    When el usuario intenta registrar un proveedor sin diligenciar los campos obligatorios
    Then el sistema muestra el mensaje de error de proveedor "Todos los campos obligatorios deben ser diligenciados"

  Scenario: Modulo no visible para roles no autorizados
    Given el usuario inicia sesion con el rol "Ventas"
    When el usuario intenta acceder al modulo de registro de proveedores
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
