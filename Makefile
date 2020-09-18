all: Jogador.class Jogo.class JogadorInterface.class JogoInterface.class

JogadorInterface.class:	src/JogadorInterface.java
	@javac src/JogadorInterface.java

JogoInterface.class: src/JogoInterface.java
	@javac src/JogoInterface.java

Jogador.class: src/Jogador.java
	@javac src/Jogador.java

Jogo.class: src/Jogo.java
	@javac src/Jogo.java

clean:
	@rm -f *.class *~
