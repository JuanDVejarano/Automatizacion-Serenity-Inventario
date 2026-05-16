@autenticacion
Feature: HU-02 Cerrar sesion del sistema
  Como usuario autenticado en el sistema
  Quiero cerrar mi sesion
  Para proteger mi cuenta al terminar de usar el sistema

  Background:
    Given el usuario tiene una sesion activa en el dashboard

  Scenario: Cierre de sesion exitoso
    When el usuario hace clic en el boton cerrar sesion
    Then el sistema elimina el token JWT del almacenamiento local
    And el sistema redirige al usuario a la pantalla de login
    And el usuario no puede acceder al dashboard sin autenticacion

  Scenario: Intento de acceso tras cerrar sesion
    Given el usuario ha cerrado su sesion
    When el usuario intenta acceder al dashboard directamente
    Then el sistema redirige automaticamente a la pantalla de login

  Scenario: Cierre de sesion detectado desde otra pestana
    Given el usuario tiene el sistema abierto en otra pestana
    When el usuario cierra sesion desde la pestana actual
    Then las demas pestanas son redirigidas al login con el mensaje "Sesion cerrada en otra pestana"
