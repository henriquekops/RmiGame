all: Jogador.class Jogo.class JogadorInterface.class JogoInterface.class

JogadorInterface.class:	src/client/JogadorInterface.java
	@javac src/client/JogadorInterface.java

JogoInterface.class: src/server/JogoInterface.java
	@javac src/server/JogoInterface.java

Jogador.class: src/client/Jogador.java
	@javac src/client/Jogador.java

Jogo.class: src/server/Jogo.java
	@javac src/server/Jogo.java

clean:
	@rm -f src/server/*.class *~
	@rm -f src/client/*.class *~
