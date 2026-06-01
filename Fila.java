public class Fila {
    String id;
    int tamanho;
    Fila proximo;

    public Fila(String id, int tamanho) {
        this.id = id;
        this.tamanho = tamanho;
        this.proximo = null;
    }
}