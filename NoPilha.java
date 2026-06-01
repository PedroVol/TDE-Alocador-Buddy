public class NoPilha {
    private Pilha topo;
    private int tamanho;

    public void empilhar(String operacao, String id, int tamanho) {
        Pilha novo = new Pilha(operacao, id, tamanho);
        novo.proximo = topo;
        topo = novo;
        tamanho++;
    }

    public Pilha desempilhar() {
        if (estaVazia()) {
            return null;
        }

        Pilha removido = topo;
        topo = topo.proximo;
        tamanho--;

        return removido;
    }

    public Pilha topo() {
        return topo;
    }

    public boolean estaVazia() {
        return topo == null;
    }

    public int tamanho() {
        return tamanho;
    }
}