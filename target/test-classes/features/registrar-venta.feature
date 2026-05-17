@ventas
Feature: HU-13 Crear venta
  Como usuario con rol Ventas o Administrador
  Quiero registrar una nueva venta en el sistema
  Para documentar las transacciones comerciales de la organizacion

  Background:
    Given el usuario ha iniciado sesion en el modulo de ventas

  Scenario: Creacion exitosa de venta
    Given existe un cliente registrado para las pruebas de venta
    When el usuario navega a registrar una nueva venta
    And el usuario selecciona el cliente de prueba en el paso 1
    And el usuario busca y agrega un producto disponible al carrito
    And el usuario confirma la venta
    Then el sistema muestra el mensaje de exito en ventas "Venta registrada exitosamente"

  Scenario: Agregar multiples productos al detalle
    Given existe un cliente registrado para las pruebas de venta
    When el usuario navega a registrar una nueva venta
    And el usuario selecciona el cliente de prueba en el paso 1
    And el usuario agrega mas de un producto al carrito de la venta
    And el usuario confirma la venta
    Then el sistema muestra el mensaje de exito en ventas "Venta registrada exitosamente"

  Scenario: Stock insuficiente
    Given existe un cliente registrado para las pruebas de venta
    When el usuario navega a registrar una nueva venta
    And el usuario selecciona el cliente de prueba en el paso 1
    And el usuario intenta agregar al carrito un producto con stock cero
    Then el sistema muestra el mensaje de error en ventas "Stock insuficiente para el producto seleccionado"

  Scenario: Venta sin productos en el detalle
    Given existe un cliente registrado para las pruebas de venta
    When el usuario navega a registrar una nueva venta
    And el usuario selecciona el cliente de prueba en el paso 1
    Then el boton de confirmar venta esta deshabilitado mientras el carrito esta vacio

  @pendiente
  Scenario: Venta completada genera movimiento en caja
    Given existe una venta creada en estado A la espera de reparto
    When el usuario cambia el estado de la venta a Completada
    Then el sistema registra un movimiento de ingreso en caja por el total de la venta

  Scenario: Venta cancelada
    Given existe un cliente registrado para las pruebas de venta
    When el usuario navega a registrar una nueva venta
    And el usuario selecciona el cliente de prueba en el paso 1
    And el usuario busca y agrega un producto disponible al carrito
    And el usuario confirma la venta
    And el usuario cancela la venta recien creada
    Then el sistema muestra el mensaje de exito en ventas "Estado de venta actualizado exitosamente"

  Scenario: Acceso no autorizado al modulo de ventas
    Given el usuario inicia sesion con el rol "Produccion"
    When el usuario intenta acceder al modulo de lista de ventas
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
