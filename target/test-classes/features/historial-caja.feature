@caja
Feature: HU-28 Consultar historial de movimientos de caja
  Como usuario con rol Tesoreria o Administrador
  Quiero consultar el historial de movimientos de la caja
  Para hacer seguimiento de todos los ingresos y egresos de la organizacion

  Background:
    Given el usuario accede al modulo de historial de caja

  Scenario: Listar todos los movimientos de caja
    Then el sistema muestra el historial de caja cargado correctamente

  Scenario: Filtrar movimientos por tipo sin resultados
    Given el historial tiene al menos un movimiento registrado
    When el usuario filtra los movimientos por un tipo que no tiene registros
    Then el sistema muestra el mensaje de historial vacio "No hay movimientos registrados con ese tipo"

  Scenario: Filtrar movimientos por fecha sin resultados
    Given el historial tiene al menos un movimiento registrado
    When el usuario aplica un rango de fechas que no coincide con ningun movimiento
    Then el sistema muestra el mensaje de historial vacio "No hay movimientos en ese rango de fechas"

  Scenario: Filtrar movimientos por tipo de operacion Ingresos
    When el usuario filtra los movimientos por tipo de operacion "Ingresos"
    Then el sistema muestra solo los movimientos de tipo ingreso o lista vacia

  Scenario: Ver detalle de un movimiento asociado a una venta
    Given existe un movimiento de historial asociado a una venta
    When el usuario selecciona el movimiento con referencia de venta
    Then el sistema muestra el detalle de la venta relacionada

  Scenario: Ver detalle de un movimiento asociado a una orden de compra
    Given existe un movimiento de historial asociado a una orden de compra
    When el usuario selecciona el movimiento con referencia de orden de compra
    Then el sistema muestra el detalle de la orden relacionada

  Scenario: Lista vacia de historial de caja
    When se simula que el historial de caja esta vacio
    Then el sistema muestra el mensaje de historial vacio "No hay movimientos registrados en el historial de caja"

  Scenario: Modulo no visible para roles no autorizados
    Given el usuario inicia sesion con el rol "Produccion"
    When el usuario intenta acceder al modulo de historial de caja
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
