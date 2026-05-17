@produccion
Feature: HU-17 Registrar producto quirurgico
  Como usuario con rol Produccion o Administrador
  Quiero registrar un nuevo producto quirurgico en el sistema
  Para mantener el catalogo de productos disponibles para la venta

  Background:
    Given el usuario ha iniciado sesion en el modulo de produccion
    And existe al menos un tipo de producto disponible para seleccionar

  Scenario: Registro exitoso de producto
    When el usuario registra un producto con todos los campos obligatorios
    Then el sistema muestra el mensaje de exito de producto "Producto registrado exitosamente"

  Scenario: Registro con caracteristicas
    When el usuario registra un producto incluyendo las caracteristicas del producto
    Then el sistema muestra el mensaje de exito de producto "Producto registrado exitosamente"

  Scenario: Precio invalido
    When el usuario intenta registrar un producto con precio cero
    Then el sistema muestra el mensaje de error de producto "El precio debe ser mayor a 0"

  Scenario: Campos obligatorios vacios
    When el usuario intenta registrar un producto sin diligenciar los campos obligatorios
    Then el sistema muestra el mensaje de error de producto "Todos los campos obligatorios deben ser diligenciados"

  Scenario: No hay tipos de producto registrados
    When se simula que no hay tipos de producto disponibles en el sistema
    Then el sistema muestra la advertencia de que no hay tipos de producto registrados
    And el boton de registrar producto esta deshabilitado

  Scenario: Acceso no autorizado al modulo de registro de productos
    Given el usuario inicia sesion con el rol "Ventas"
    When el usuario intenta acceder al modulo de registro de productos
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
