@ventas
Feature: HU-14 Agregar productos al detalle de venta
  Como usuario con rol Ventas o Administrador
  Quiero agregar productos al detalle de una venta mediante busqueda o lector de codigos de barras
  Para agilizar el proceso de registro de productos en una venta

  Background:
    Given el usuario ha iniciado sesion en el modulo de ventas
    And el usuario esta en el formulario de nueva venta con cliente seleccionado

  Scenario: Agregar producto por busqueda manual
    When el usuario busca un producto por nombre y selecciona el primero disponible
    Then el producto aparece en el detalle de la venta con total mayor a cero

  Scenario: Agregar producto por codigo de barras
    When el usuario agrega un producto usando su ID como codigo de barras
    Then el producto aparece en el detalle de la venta con total mayor a cero

  Scenario: Codigo de barras no reconocido
    When el usuario ingresa un codigo de barras que no existe en el sistema
    Then el sistema muestra el mensaje de error en ventas "Producto no encontrado"

  Scenario: Modificar cantidad de un producto en el detalle
    Given el usuario ha agregado un producto al carrito de venta
    When el usuario modifica la cantidad del producto en el detalle
    Then el campo de cantidad refleja el cambio y el total sigue siendo mayor a cero

  Scenario: Eliminar producto del detalle
    Given el usuario ha agregado un producto al carrito de venta
    When el usuario elimina el producto del carrito de venta
    Then el carrito de la venta queda vacio

  Scenario: Acceso no autorizado al modulo de ventas
    Given el usuario inicia sesion con el rol "Produccion"
    When el usuario intenta acceder al modulo de lista de ventas
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
