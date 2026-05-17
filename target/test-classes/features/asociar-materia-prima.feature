@materiaPrima
Feature: HU-21 Asociar materia prima a producto
  Como usuario con rol Produccion o Administrador
  Quiero asociar materias primas a un producto quirurgico
  Para definir los insumos necesarios para la fabricacion de cada producto

  Background:
    Given el usuario accede al modulo de asociar materia prima

  Scenario: Asociacion exitosa de materia prima a producto
    When el usuario selecciona el primer producto y asocia la primera materia prima con cantidad 5
    Then la materia prima queda registrada en la lista de asociaciones del producto

  Scenario: Asociar multiples materias primas a un producto
    When el usuario selecciona el primer producto y asocia la primera materia prima con cantidad 3
    And el usuario asocia la segunda materia prima disponible con cantidad 2
    Then el sistema muestra 2 materias primas asociadas al producto

  Scenario: Materia prima ya asociada al producto
    Given existe al menos una materia prima asociada al producto seleccionado
    When el usuario intenta asociar la misma materia prima al mismo producto nuevamente
    Then el sistema muestra el mensaje de error de asociacion "Esa materia prima ya esta asociada a este producto"

  Scenario: Cantidad invalida al asociar
    When el usuario selecciona el primer producto y una materia prima con cantidad invalida de cero
    Then el sistema muestra el mensaje de error de asociacion "La cantidad debe ser mayor a 0"

  Scenario: Editar cantidad de materia prima asociada
    Given existe al menos una materia prima asociada al producto seleccionado
    When el usuario edita la cantidad de la primera materia prima asociada a 10
    Then el sistema muestra el mensaje de exito de edicion de asociacion "Cantidad actualizada exitosamente"

  Scenario: Eliminar asociacion de materia prima
    Given existe al menos una materia prima asociada al producto seleccionado
    When el usuario elimina la primera materia prima asociada al producto
    Then la asociacion queda eliminada de la lista del producto

  Scenario: No hay materias primas registradas
    When el usuario selecciona el primer producto y no hay materias primas en el sistema
    Then el sistema muestra la advertencia de que no hay materias primas disponibles

  Scenario: Modulo no visible para roles no autorizados
    Given el usuario inicia sesion con el rol "Ventas"
    When el usuario intenta acceder al modulo de asociar materia prima a producto
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
