@IA
Feature: HU-30 Agente LangGraph con servidores MCP
  Como usuario con rol Produccion, Tesoreria o Administrador
  Quiero interactuar con un agente inteligente que encadene herramientas MCP
  Para resolver consultas complejas en una sola conversacion

  Scenario: El agente encadena tools automaticamente ante una consulta en lenguaje natural
    Given el agente LangGraph esta disponible y conectado a los servidores MCP
    When el usuario envia un mensaje en lenguaje natural al agente
    Then el agente invoca las tools necesarias en orden y responde con una sintesis

  Scenario: Control de acceso por rol devuelve 403 para roles no autorizados
    Given el agente LangGraph esta disponible y conectado a los servidores MCP
    When un usuario con rol no autorizado intenta usar el agente
    Then el endpoint retorna 403 y el agente no se instancia

  Scenario: Consulta de produccion encadenada con multiples tools
    Given el agente LangGraph esta disponible y conectado a los servidores MCP
    When el usuario con rol Produccion consulta si puede fabricar una cantidad de un producto
    Then el agente encadena verificar_materiales, generar_opciones y validar_presupuesto automaticamente

  Scenario: Confirmacion del agente antes de crear una orden de compra
    Given el agente LangGraph esta disponible y conectado a los servidores MCP
    When el agente determina que debe invocar crear_orden_compra
    Then el agente pausa y solicita confirmacion al usuario antes de ejecutar la accion
    And la respuesta del endpoint incluye el campo requiresConfirmation en true
