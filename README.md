2048
====
Este programa fue creado para resolver el Desafio Red Hat 2048.

Para esto se conecta al servidor proporcionado por RedHat para obtener el Board y los aleatorios que se van agregando al mismo.

De cualquier manera es programa es 100% funcional sin el uso de los servidores, solo se necesita realizar un par de cambios.

La Ia funciona con el Algoritmo de Expectimax, tambien estuvimos testeando con Min-Max y Alpha-Beta, si bien ambos algoritmos demostraron ser mas rapidos en tiempo de desarrollo que Expectimax, este ultimo demostro ser mucho mas preciso, dando asi un mayor porcentaje de juegos ganados que los otros dos. Se deberia buscar una manera de mejorar la velocidad de dicho Algoritmos para poder alcanzar una profundidad mayor, ya que con una profundidad de 8 (si bien muy lento) demuestra un 100% de juegos ganados. Actualmente la profundidad de la Ai se seteo en 5 para no superar la barrera de 2 minutos propuesta por el desafio.
