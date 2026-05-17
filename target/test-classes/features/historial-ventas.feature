@ventas
Feature: HU-15 Consultar historial de ventas
  Como usuario con rol Ventas o Administrador
  Quiero consultar el historial de ventas realizadas
  Para hacer seguimiento de las transacciones comerciales de la organizacion

  Background:
    Given el usuario ha iniciado sesion en el modulo de ventas
    And el usuario navega al historial de ventas

  Scenario: Listar todas las ventas con sus datos
    Given existe al menos una venta registrada en el historial
    Then el sistema muestra la tabla de ventas con las columnas requeridas
    And la tabla de ventas contiene al menos una venta

  Scenario: Filtrar ventas por estado
    Given existe al menos una venta registrada en el historial
    When el usuario filtra las ventas por estado "A la espera de reparto"
    Then la tabla de ventas contiene al menos una venta

  Scenario: Filtrar ventas por rango de fechas sin resultados
    When el usuario filtra las ventas por un rango de fechas futuro
    Then el sistema muestra en el historial el mensaje "No hay ventas en ese rango de fechas"

  Scenario: Buscar venta por cliente sin coincidencias
    When el usuario busca en el historial ventas del cliente "XYZNOEXISTEENVENTA99"
    Then el sistema muestra en el historial el mensaje "No se encontraron ventas para ese cliente"

  Scenario: Ver detalle de una venta
    Given existe al menos una venta registrada en el historial
    When el usuario abre el detalle de la primera venta de la lista
    Then el sistema muestra el modal con el detalle de la venta

  Scenario: Cambiar estado de una venta
    Given existe una venta disponible para cambio de estado en el historial
    When el usuario cambia la venta al siguiente estado disponible
    Then el sistema muestra el mensaje de exito en ventas "Estado de venta actualizado exitosamente"

  @pendiente
  Scenario: Lista vacia de ventas
    When el historial no tiene ventas registradas para esta prueba
    Then el sistema muestra en el historial el mensaje "No hay ventas registradas"

  Scenario: Acceso no autorizado al historial de ventas
    Given el usuario inicia sesion con el rol "Produccion"
    When el usuario intenta acceder al modulo de lista de ventas
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
