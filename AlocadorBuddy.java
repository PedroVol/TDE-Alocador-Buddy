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

    private No copiarArvore(No original, No pai) {
        if (original == null) {
            return null;
        }

        No copia = new No(original.tamanho, pai);
        copia.estado = original.estado;
        copia.id = original.id;

        copia.esquerdo = copiarArvore(original.esquerdo, copia);
        copia.direito = copiarArvore(original.direito, copia);

        return copia;
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

        No estadoAnterior = copiarArvore(raiz, null);

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
            historico.empilhar("ALOCAR", id, tamanhoSolicitado, estadoAnterior);
        }

        return true;
    }

    private No buscarBlocoLivre(No atual, int tamanhoNecessario) {
        int indiceNecessario = indiceLista(tamanhoNecessario);

        for (int i = indiceNecessario; i < listasLivres.length; i++) {
            if (!listasLivres[i].estaVazia()) {
                No bloco = listasLivres[i].buscarPrimeiro();

                while (bloco.tamanho > tamanhoNecessario) {
                    dividirComLista(bloco);
                    bloco = bloco.esquerdo;
                }

                listasLivres[indiceLista(bloco.tamanho)].remover(bloco);
                return bloco;
            }
        }

        return null;
    }

    private void dividirComLista(No no) {
        listasLivres[indiceLista(no.tamanho)].remover(no);

        dividir(no);

        int indiceFilhos = indiceLista(no.esquerdo.tamanho);

        listasLivres[indiceFilhos].inserir(no.esquerdo);
        listasLivres[indiceFilhos].inserir(no.direito);
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

        No estadoAnterior = copiarArvore(raiz, null);

        int tamanhoBloco = bloco.tamanho;

        bloco.estado = "LIVRE";
        bloco.id = null;

        System.out.println("Liberado: " + id);

        tentarMerge(bloco);
        atualizarListasLivres();

        if (registrarHistorico) {
            historico.empilhar("LIBERAR", id, tamanhoBloco, estadoAnterior);
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

            listasLivres[indiceLista(esquerdo.tamanho)].remover(esquerdo);
            listasLivres[indiceLista(direito.tamanho)].remover(direito);

            pai.esquerdo = null;
            pai.direito = null;
            pai.estado = "LIVRE";
            pai.id = null;

            listasLivres[indiceLista(pai.tamanho)].inserir(pai);

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

            No estadoAnterior = copiarArvore(raiz, null);

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

                historico.empilhar("ALOCAR", pendente.id, pendente.tamanho, estadoAnterior);
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

        System.out.println("Desfazendo operacao: " + ultima.operacao + " " + ultima.id);

        raiz = ultima.estadoAnterior;

        atualizarListasLivres();

        System.out.println("Estado anterior restaurado com sucesso.");
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
