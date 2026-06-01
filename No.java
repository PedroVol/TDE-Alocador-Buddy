public class No {
    int tamanho;
    String estado;
    String id;

    No pai;
    No esquerdo;
    No direito;

    public No(int tamanho, No pai) {
        this.tamanho = tamanho;
        this.pai = pai;
        this.estado = "LIVRE";
        this.id = null;
    }

    public boolean naoDividido() {
        return esquerdo == null && direito == null;
    }
}