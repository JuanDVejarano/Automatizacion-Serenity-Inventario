@IA
Feature: HU-31 Chat del agente en el frontend Angular
  Como usuario con rol Produccion, Tesoreria o Administrador
  Quiero acceder a un chat conversacional dentro del sistema
  Para interactuar con el agente inteligente sin salir de la plataforma

  Scenario: Interfaz del chat carga con todos sus elementos
    Given el usuario accede al modulo del chat del agente
    When la pagina termina de cargar
    Then se muestra el area de historial, el campo de texto y el boton de envio
    And el boton de envio esta deshabilitado cuando el campo de texto esta vacio

  Scenario: Indicador de carga visible mientras el agente procesa la respuesta
    Given el usuario accede al modulo del chat del agente
    When el usuario envia un mensaje y el agente esta procesando
    Then se muestra el indicador de escritura animado y los controles quedan deshabilitados

  Scenario: El chat muestra botones de confirmacion cuando el agente lo solicita
    Given el usuario accede al modulo del chat del agente
    When el agente solicita confirmacion antes de ejecutar una accion
    Then se muestran los botones Confirmar y Cancelar debajo del mensaje del agente

  Scenario: Modulo del chat no visible para roles no autorizados
    Given el usuario accede al sistema con un rol no autorizado
    When intenta acceder a la ruta del agente directamente por URL
    Then el sistema redirige al dashboard sin mostrar el chat
