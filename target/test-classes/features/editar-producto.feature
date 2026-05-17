@produccion
Feature: HU-18 Editar producto quirurgico
  Como usuario con rol Produccion o Administrador
  Quiero editar la informacion y el stock de un producto quirurgico
  Para mantener el catalogo de productos actualizado

  Background:
    Given el usuario ha iniciado sesion en el modulo de produccion
    And existe al menos un producto disponible para editar

  Scenario: Edicion exitosa de informacion del producto
    When el usuario selecciona el primer producto para editar su informacion
    And el usuario modifica el nombre del producto
    And el usuario guarda los cambios del producto
    Then el sistema muestra el mensaje de exito de edicion de producto "Producto actualizado exitosamente"

  Scenario: Actualizacion de stock manualmente
    When el usuario selecciona el primer producto para actualizar su stock
    And el usuario ingresa una cantidad valida de unidades al stock
    And el usuario confirma la actualizacion de stock
    Then el sistema muestra el mensaje de exito de stock "Stock actualizado exitosamente"

  Scenario: Actualizacion de stock por codigo de barras
    When el sistema identifica el primer producto por su codigo de barras
    And el usuario ingresa una cantidad valida de unidades al stock
    And el usuario confirma la actualizacion de stock
    Then el sistema muestra el mensaje de exito de stock "Stock actualizado exitosamente"

  Scenario: Codigo de barras no reconocido
    When el usuario escanea un codigo de barras inexistente en el sistema
    Then el sistema muestra el mensaje de barcode no encontrado "Producto no encontrado"

  Scenario: Cantidad de stock invalida
    When el usuario selecciona el primer producto para actualizar su stock
    And el usuario ingresa una cantidad invalida de cero unidades
    And el usuario confirma la actualizacion de stock
    Then el sistema muestra el mensaje de error de stock "La cantidad debe ser mayor a 0"

  Scenario: Precio invalido al editar
    When el usuario selecciona el primer producto para editar su informacion
    And el usuario establece un precio invalido de cero
    And el usuario guarda los cambios del producto
    Then el sistema muestra el mensaje de error de edicion de producto "El precio debe ser mayor a 0"

  Scenario: Campos obligatorios vacios al editar
    When el usuario selecciona el primer producto para editar su informacion
    And el usuario borra el nombre del producto
    And el usuario guarda los cambios del producto
    Then el sistema muestra el mensaje de error de edicion de producto "Todos los campos obligatorios deben ser diligenciados"

  Scenario: Acceso no autorizado al modulo de edicion de productos
    Given el usuario inicia sesion con el rol "Ventas"
    When el usuario intenta acceder al modulo de edicion de productos
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
