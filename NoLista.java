public class NoLista {
    private Lista inicio;
    private int tamanho;

    public void inserir(No bloco) {
        if (bloco == null) {
            return;
        }

        if (contem(bloco)) {
            return;
        }

        Lista novo = new Lista(bloco);
        novo.proximo = inicio;
        inicio = novo;
        tamanho++;
    }

    public void remover(No bloco) {
        if (inicio == null || bloco == null) {
            return;
        }

        if (inicio.bloco == bloco) {
            inicio = inicio.proximo;
            tamanho--;
            return;
        }

        Lista atual = inicio;

        while (atual.proximo != null) {
            if (atual.proximo.bloco == bloco) {
                atual.proximo = atual.proximo.proximo;
                tamanho--;
                return;
            }

            atual = atual.proximo;
        }
    }

    public No buscarPrimeiro() {
        if (inicio == null) {
            return null;
        }

        return inicio.bloco;
    }

    public boolean contem(No bloco) {
        Lista atual = inicio;

        while (atual != null) {
            if (atual.bloco == bloco) {
                return true;
            }

            atual = atual.proximo;
        }

        return false;
    }

    public boolean estaVazia() {
        return inicio == null;
    }

    public int tamanho() {
        return tamanho;
    }

    public void exibir() {
        if (estaVazia()) {
            System.out.println("vazia");
            return;
        }

        Lista atual = inicio;

        while (atual != null) {
            System.out.print("[" + atual.bloco.tamanho + " KB LIVRE] ");
            atual = atual.proximo;
        }

        System.out.println();
    }
}