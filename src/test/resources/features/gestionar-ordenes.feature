@ordenesDeCompra
Feature: HU-25 Consultar y gestionar ordenes de compra
  Como usuario con rol Produccion o Administrador
  Quiero consultar y gestionar las ordenes de compra del sistema
  Para hacer seguimiento de los pedidos de materia prima y actualizar su estado

  Background:
    Given el usuario accede al modulo de gestionar ordenes de compra

  Scenario: Listar todas las ordenes de compra
    Then el sistema muestra el modulo de ordenes de compra cargado correctamente

  Scenario: Ver detalle de una orden de compra
    Given existe al menos una orden de compra en el sistema
    When el usuario selecciona la primera orden de la lista
    Then el sistema muestra el panel de detalle de la orden

  Scenario: Filtrar ordenes por estado Pendiente
    When el usuario filtra las ordenes de compra por estado "Pendiente"
    Then el sistema muestra unicamente las ordenes con el estado seleccionado

  Scenario: Filtrar ordenes por fecha sin resultados
    When el usuario aplica un filtro de fechas que no coincide con ninguna orden
    Then el sistema muestra el mensaje de lista vacia por rango de fechas "No hay ordenes de compra en ese rango de fechas"

  Scenario: Cambiar estado de orden a Completada
    Given existe una orden de compra pendiente para gestionar
    When el usuario marca la primera orden pendiente como Completada
    Then el sistema muestra el mensaje de exito de cambio de estado "Estado de orden actualizado exitosamente"

  Scenario: Cancelar una orden de compra
    Given existe una orden de compra pendiente para gestionar
    When el usuario cancela la primera orden pendiente
    Then el sistema muestra el mensaje de exito de cambio de estado "Orden de compra cancelada exitosamente"

  @pendiente
  Scenario: Orden completada descuenta capital de caja
    Given existe una orden pendiente con capital suficiente en caja
    When el usuario marca la orden como Completada con caja configurada
    Then el sistema descuenta el costo del capital de caja
    And el sistema actualiza el stock de las materias primas de la orden

  @pendiente
  Scenario: Lista vacia de ordenes de compra
    Given no hay ordenes de compra registradas en el sistema
    Then el sistema muestra el mensaje "No hay ordenes de compra registradas"

  Scenario: Modulo no visible para roles no autorizados
    Given el usuario inicia sesion con el rol "Ventas"
    When el usuario intenta acceder al modulo de gestionar ordenes de compra
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
