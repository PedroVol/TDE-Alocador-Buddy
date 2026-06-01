public class Pilha {
    String operacao;
    String id;
    int tamanho;
    Pilha proximo;

    public Pilha(String operacao, String id, int tamanho) {
        this.operacao = operacao;
        this.id = id;
        this.tamanho = tamanho;
        this.proximo = null;
    }
}