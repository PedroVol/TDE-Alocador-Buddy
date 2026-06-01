public class AlocadorBuddy {
    private No raiz;

    private final int MEMORIA_TOTAL = 32768;
    private final int BUDDY_MINIMO = 4;

    private NoFila filaPendentes;
    private NoPilha historico;
    private NoLista[] listasLivres;

    public AlocadorBuddy() {
        raiz = new No(MEMORIA_TOTAL, null);
        filaPendentes = new NoFila();
        historico = new NoPilha();

        criarListasLivres();
        atualizarListasLivres();
    }

    private void criarListasLivres() {
        listasLivres = new NoLista[14];

        for (int i = 0; i < listasLivres.length; i++) {
            listasLivres[i] = new NoLista();
        }
    }

    private int indiceLista(int tamanho) {
        int indice = 0;
        int valor = BUDDY_MINIMO;

        while (valor < tamanho) {
            valor *= 2;
            indice++;
        }

        return indice;
    }

    private void limparListasLivres() {
        for (int i = 0; i < listasLivres.length; i++) {
            listasLivres[i] = new NoLista();
        }
    }

    private void atualizarListasLivres() {
        limparListasLivres();
        preencherListasLivres(raiz);
    }

    private void preencherListasLivres(No atual) {
        if (atual == null) {
            return;
        }

        if (atual.estado.equals("LIVRE") && atual.naoDividido()) {
            int indice = indiceLista(atual.tamanho);
            listasLivres[indice].inserir(atual);
            return;
        }

        preencherListasLivres(atual.esquerdo);
        preencherListasLivres(atual.direito);
    }

    public void alocar(String id, int tamanhoSolicitado) {
        alocarInterno(id, tamanhoSolicitado, true);
    }

    private boolean alocarInterno(String id, int tamanhoSolicitado, boolean registrarHistorico) {
        if (buscarPorId(raiz, id) != null) {
            System.out.println("Erro: ja existe um bloco com o ID " + id);
            return false;
        }

        int tamanhoReal = proximaPotenciaDeDois(tamanhoSolicitado);

        if (tamanhoReal < BUDDY_MINIMO) {
            tamanhoReal = BUDDY_MINIMO;
        }

        if (tamanhoReal > MEMORIA_TOTAL) {
            System.out.println("Erro: tamanho maior que a memoria total.");
            return false;
        }

        No bloco = buscarBlocoLivre(raiz, tamanhoReal);

        if (bloco == null) {
            System.out.println("Falha ao alocar " + id + ". Enviado para fila de pendentes.");
            filaPendentes.enfileirar(id, tamanhoSolicitado);
            return false;
        }

        bloco.estado = "OCUPADO";
        bloco.id = id;

        atualizarListasLivres();

        int desperdicio = tamanhoReal - tamanhoSolicitado;

        System.out.println("Alocado: " + id);
        System.out.println("Solicitado: " + tamanhoSolicitado + " KB");
        System.out.println("Bloco usado: " + tamanhoReal + " KB");
        System.out.println("Fragmentacao interna: " + desperdicio + " KB");

        if (registrarHistorico) {
            historico.empilhar("ALOCAR", id, tamanhoSolicitado);
        }

        return true;
    }

    private No buscarBlocoLivre(No atual, int tamanhoNecessario) {
        if (atual == null) {
            return null;
        }

        if (atual.estado.equals("OCUPADO")) {
            return null;
        }

        if (atual.estado.equals("DIVIDIDO")) {
            No esquerda = buscarBlocoLivre(atual.esquerdo, tamanhoNecessario);

            if (esquerda != null) {
                return esquerda;
            }

            return buscarBlocoLivre(atual.direito, tamanhoNecessario);
        }

        if (atual.estado.equals("LIVRE")) {
            if (atual.tamanho == tamanhoNecessario) {
                return atual;
            }

            if (atual.tamanho > tamanhoNecessario && atual.tamanho / 2 >= BUDDY_MINIMO) {
                dividir(atual);
                return buscarBlocoLivre(atual.esquerdo, tamanhoNecessario);
            }
        }

        return null;
    }

    private void dividir(No no) {
        no.estado = "DIVIDIDO";
        no.id = null;

        no.esquerdo = new No(no.tamanho / 2, no);
        no.direito = new No(no.tamanho / 2, no);
    }

    public void liberar(String id) {
        liberarInterno(id, true);
    }

    private boolean liberarInterno(String id, boolean registrarHistorico) {
        No bloco = buscarPorId(raiz, id);

        if (bloco == null) {
            System.out.println("Erro: bloco " + id + " nao encontrado.");
            return false;
        }

        int tamanhoBloco = bloco.tamanho;

        bloco.estado = "LIVRE";
        bloco.id = null;

        System.out.println("Liberado: " + id);

        tentarMerge(bloco);
        atualizarListasLivres();

        if (registrarHistorico) {
            historico.empilhar("LIBERAR", id, tamanhoBloco);
        }

        tentarAtenderFila();

        return true;
    }

    private No buscarPorId(No atual, String id) {
        if (atual == null) {
            return null;
        }

        if (atual.estado.equals("OCUPADO") && id.equals(atual.id)) {
            return atual;
        }

        No esquerda = buscarPorId(atual.esquerdo, id);

        if (esquerda != null) {
            return esquerda;
        }

        return buscarPorId(atual.direito, id);
    }

    private void tentarMerge(No no) {
        if (no == null || no.pai == null) {
            return;
        }

        No pai = no.pai;
        No esquerdo = pai.esquerdo;
        No direito = pai.direito;

        if (esquerdo != null &&
            direito != null &&
            esquerdo.estado.equals("LIVRE") &&
            direito.estado.equals("LIVRE") &&
            esquerdo.naoDividido() &&
            direito.naoDividido()) {

            pai.esquerdo = null;
            pai.direito = null;
            pai.estado = "LIVRE";
            pai.id = null;

            tentarMerge(pai);
        }
    }

private void tentarAtenderFila() {
    int quantidadeInicial = filaPendentes.tamanho();

    for (int i = 0; i < quantidadeInicial; i++) {
        Fila pendente = filaPendentes.desenfileirar();

        int tamanhoReal = proximaPotenciaDeDois(pendente.tamanho);

        if (tamanhoReal < BUDDY_MINIMO) {
            tamanhoReal = BUDDY_MINIMO;
        }

        No bloco = buscarBlocoLivre(raiz, tamanhoReal);

        if (bloco == null) {
            filaPendentes.enfileirar(pendente.id, pendente.tamanho);
        } else {
            System.out.println("Atendendo pendente da fila: " + pendente.id);

            bloco.estado = "OCUPADO";
            bloco.id = pendente.id;

            atualizarListasLivres();

            int desperdicio = tamanhoReal - pendente.tamanho;

            System.out.println("Alocado: " + pendente.id);
            System.out.println("Solicitado: " + pendente.tamanho + " KB");
            System.out.println("Bloco usado: " + tamanhoReal + " KB");
            System.out.println("Fragmentacao interna: " + desperdicio + " KB");

            historico.empilhar("ALOCAR", pendente.id, pendente.tamanho);
        }
    }

    atualizarListasLivres();
}

    public void desfazer() {
        if (historico.estaVazia()) {
            System.out.println("Historico vazio. Nada para desfazer.");
            return;
        }

        Pilha ultima = historico.desempilhar();

        if (ultima.operacao.equals("ALOCAR")) {
            System.out.println("Desfazendo alocacao de " + ultima.id);
            liberarInterno(ultima.id, false);
        } else if (ultima.operacao.equals("LIBERAR")) {
            System.out.println("Desfazendo liberacao de " + ultima.id);
            alocarInterno(ultima.id, ultima.tamanho, false);
        }

        atualizarListasLivres();
    }

    private int proximaPotenciaDeDois(int valor) {
        int potencia = 1;

        while (potencia < valor) {
            potencia *= 2;
        }

        return potencia;
    }

    public void exibirMemoria() {
        System.out.println("\nEstado da memoria:");
        exibirNo(raiz, 0);
    }

    private void exibirNo(No no, int nivel) {
        if (no == null) {
            return;
        }

        for (int i = 0; i < nivel; i++) {
            System.out.print("   ");
        }

        if (no.estado.equals("OCUPADO")) {
            System.out.println("[" + no.tamanho + " KB OCUPADO id=" + no.id + "]");
        } else {
            System.out.println("[" + no.tamanho + " KB " + no.estado + "]");
        }

        exibirNo(no.esquerdo, nivel + 1);
        exibirNo(no.direito, nivel + 1);
    }

    public void exibirFila() {
        filaPendentes.exibir();
    }

    public void exibirListasLivres() {
        atualizarListasLivres();

        System.out.println("\nListas de blocos livres:");

        int tamanho = BUDDY_MINIMO;

        for (int i = 0; i < listasLivres.length; i++) {
            System.out.print("Lista " + tamanho + " KB: ");
            listasLivres[i].exibir();
            tamanho *= 2;
        }
    }
}