public class NoFila {
    private Fila inicio;
    private Fila fim;
    private int tamanho;

    public void enfileirar(String id, int tamanhoKb) {
        Fila novo = new Fila(id, tamanhoKb);

        if (estaVazia()) {
            inicio = novo;
            fim = novo;
        } else {
            fim.proximo = novo;
            fim = novo;
        }

        tamanho++;
    }

    public Fila desenfileirar() {
        if (estaVazia()) {
            return null;
        }

        Fila removido = inicio;
        inicio = inicio.proximo;

        if (inicio == null) {
            fim = null;
        }

        tamanho--;
        return removido;
    }

    public Fila espiar() {
        return inicio;
    }

    public boolean estaVazia() {
        return inicio == null;
    }

    public int tamanho() {
        return tamanho;
    }

    public void exibir() {
        if (estaVazia()) {
            System.out.println("Fila de pendentes vazia.");
            return;
        }

        System.out.println("Fila de pendentes:");

        Fila atual = inicio;

        while (atual != null) {
            System.out.println("ID: " + atual.id + " | Tamanho: " + atual.tamanho + " KB");
            atual = atual.proximo;
        }
    }
}