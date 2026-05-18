@proveedores
Feature: HU-23 Asociar proveedor a materia prima
  Como usuario con rol Produccion o Administrador
  Quiero asociar proveedores a las materias primas del sistema
  Para definir que proveedores pueden suministrar cada insumo y a que costo

  Background:
    Given el usuario accede al modulo de asociar proveedor a materia prima

  Scenario: Asociacion exitosa de proveedor a materia prima
    When el usuario selecciona el primer proveedor y asocia la primera materia prima con costo 5000
    Then la materia prima queda registrada en la lista de asociaciones del proveedor

  Scenario: Asociar multiples materias primas a un proveedor
    When el usuario selecciona el primer proveedor y asocia la primera materia prima con costo 3000
    And el usuario asocia la segunda materia prima disponible al proveedor con costo 2000
    Then el sistema muestra al menos 2 materias primas asociadas al proveedor

  Scenario: Asociacion duplicada de proveedor a materia prima
    Given existe al menos una materia prima asociada al proveedor seleccionado
    When el usuario intenta asociar la misma materia prima al mismo proveedor nuevamente
    Then el sistema muestra el mensaje de error de asociacion proveedor "Ese proveedor ya esta asociado a esa materia prima"

  Scenario: Costo por unidad invalido
    When el usuario selecciona el primer proveedor y una materia prima con costo invalido de cero
    Then el sistema muestra el mensaje de error de asociacion proveedor "El costo por unidad debe ser mayor a 0"

  Scenario: Editar costo por unidad de asociacion existente
    Given existe al menos una materia prima asociada al proveedor seleccionado
    When el usuario edita el costo de la primera asociacion del proveedor a 9500
    Then el sistema muestra el mensaje de exito de edicion de asociacion proveedor "Costo actualizado exitosamente"

  Scenario: Eliminar asociacion de proveedor a materia prima
    Given existe al menos una materia prima asociada al proveedor seleccionado
    When el usuario elimina la primera asociacion del proveedor a materia prima
    Then la asociacion queda eliminada de la lista del proveedor

  Scenario: No hay materias primas registradas para el proveedor
    When el usuario selecciona el primer proveedor y no hay materias primas en el sistema
    Then el sistema muestra la advertencia de que no hay materias primas disponibles para el proveedor

  Scenario: Modulo no visible para roles no autorizados
    Given el usuario inicia sesion con el rol "Ventas"
    When el usuario intenta acceder al modulo de asociar proveedor a materia prima
    Then el sistema muestra la pagina de acceso denegado
    And el sistema muestra el mensaje "No tiene permisos para acceder a este modulo"
