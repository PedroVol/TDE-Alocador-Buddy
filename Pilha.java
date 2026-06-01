public class Pilha {
    String operacao;
    String id;
    int tamanho;
    No estadoAnterior;
    Pilha proximo;

    public Pilha(String operacao, String id, int tamanho, No estadoAnterior) {
        this.operacao = operacao;
        this.id = id;
        this.tamanho = tamanho;
        this.proximo = null;
        this.estadoAnterior = estadoAnterior;
    }
}
