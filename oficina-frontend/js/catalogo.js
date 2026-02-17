/**
 * ═══════════════════════════════════════════════════════
 *  Oficina Digital — Módulo de Catálogo (Peças & Serviços)
 *  CRUD para /api/v1/pecas e /api/v1/servicos (Catalog Service :8085)
 * ═══════════════════════════════════════════════════════
 */
const CatalogoModule = (() => {

    const render = () => `
        <div class="panel-header">
            <h1><span class="accent-catalog">●</span> Catálogo</h1>
            <span class="service-tag">catalog-service :8085</span>
        </div>

        <div class="sub-tabs">
            <button class="sub-tab active" data-subtab="pecas" onclick="CatalogoModule.switchTab('pecas')">🔩 Peças</button>
            <button class="sub-tab" data-subtab="servicos" onclick="CatalogoModule.switchTab('servicos')">🛠️ Serviços</button>
        </div>

        <!-- Peças Sub-Panel -->
        <div class="sub-panel active" id="subtab-pecas">
            <div class="panel-grid">
                <div class="card">
                    <h3>➕ Cadastrar Peça</h3>
                    <div class="form-grid">
                        <div class="form-group">
                            <label>Nome *</label>
                            <input type="text" id="peca-nome" placeholder="Ex: Filtro de Óleo">
                        </div>
                        <div class="form-group">
                            <label>Código Fabricante *</label>
                            <input type="text" id="peca-codigo" placeholder="Ex: FO-001">
                        </div>
                        <div class="form-group">
                            <label>Preço (R$) *</label>
                            <input type="number" id="peca-preco" placeholder="0.00" step="0.01" min="0.01">
                        </div>
                        <div class="form-group">
                            <label>Quantidade *</label>
                            <input type="number" id="peca-qtd" placeholder="10" min="1">
                        </div>
                        <div class="form-group">
                            <label>Qtd Mínima</label>
                            <input type="number" id="peca-qtd-min" placeholder="5" min="1" value="5">
                        </div>
                        <div class="form-group full-width">
                            <label>Descrição</label>
                            <input type="text" id="peca-desc" placeholder="Descrição da peça (opcional)">
                        </div>
                    </div>
                    <button class="btn btn-primary" onclick="CatalogoModule.criarPeca()">Cadastrar Peça</button>
                </div>

                <div class="card">
                    <h3>🔍 Buscar Peças</h3>
                    <div class="form-grid">
                        <div class="form-group">
                            <label>Buscar por ID</label>
                            <input type="text" id="peca-searchId" placeholder="ID da peça">
                        </div>
                        <div class="form-group">
                            <label>Filtrar por Categoria</label>
                            <input type="text" id="peca-filterCategoria" placeholder="Categoria">
                        </div>
                        <div class="form-group">
                            <label>Filtrar por Marca</label>
                            <input type="text" id="peca-filterMarca" placeholder="Marca">
                        </div>
                    </div>
                    <div class="btn-row">
                        <button class="btn btn-secondary" onclick="CatalogoModule.buscarPecaPorId()">Por ID</button>
                        <button class="btn btn-secondary" onclick="CatalogoModule.listarPecas()">Listar Todas</button>
                        <button class="btn btn-secondary" onclick="CatalogoModule.listarPecasAtivas()">Ativas</button>
                        <button class="btn btn-secondary" onclick="CatalogoModule.buscarPecaCategoria()">Por Categoria</button>
                        <button class="btn btn-secondary" onclick="CatalogoModule.buscarPecaMarca()">Por Marca</button>
                    </div>
                </div>

                <div class="card full-width">
                    <div class="card-header-actions">
                        <h3>🔩 Peças Cadastradas</h3>
                        <button class="btn btn-ghost btn-sm" onclick="CatalogoModule.listarPecas()">🔄 Atualizar</button>
                    </div>
                    <div id="pecas-results" class="results-area">
                        <p class="placeholder">Nenhum resultado ainda.</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Serviços Sub-Panel -->
        <div class="sub-panel" id="subtab-servicos">
            <div class="panel-grid">
                <div class="card">
                    <h3>➕ Cadastrar Serviço</h3>
                    <div class="form-grid">
                        <div class="form-group">
                            <label>Nome *</label>
                            <input type="text" id="servico-nome" placeholder="Ex: Troca de Óleo">
                        </div>
                        <div class="form-group">
                            <label>Preço (R$) *</label>
                            <input type="number" id="servico-preco" placeholder="0.00" step="0.01" min="0.01">
                        </div>
                        <div class="form-group">
                            <label>Tempo Estimado (min) *</label>
                            <input type="number" id="servico-tempo" placeholder="60" min="1">
                        </div>
                        <div class="form-group full-width">
                            <label>Descrição</label>
                            <input type="text" id="servico-desc" placeholder="Descrição do serviço (opcional)">
                        </div>
                    </div>
                    <button class="btn btn-primary" onclick="CatalogoModule.criarServico()">Cadastrar Serviço</button>
                </div>

                <div class="card">
                    <h3>🔍 Buscar Serviços</h3>
                    <div class="form-grid">
                        <div class="form-group">
                            <label>Buscar por ID</label>
                            <input type="text" id="servico-searchId" placeholder="ID do serviço">
                        </div>
                        <div class="form-group">
                            <label>Filtrar por Categoria</label>
                            <input type="text" id="servico-filterCategoria" placeholder="Categoria">
                        </div>
                    </div>
                    <div class="btn-row">
                        <button class="btn btn-secondary" onclick="CatalogoModule.buscarServicoPorId()">Por ID</button>
                        <button class="btn btn-secondary" onclick="CatalogoModule.listarServicos()">Listar Todos</button>
                        <button class="btn btn-secondary" onclick="CatalogoModule.listarServicosAtivos()">Ativos</button>
                        <button class="btn btn-secondary" onclick="CatalogoModule.buscarServicoCategoria()">Por Categoria</button>
                    </div>
                </div>

                <div class="card full-width">
                    <div class="card-header-actions">
                        <h3>🛠️ Serviços Cadastrados</h3>
                        <button class="btn btn-ghost btn-sm" onclick="CatalogoModule.listarServicos()">🔄 Atualizar</button>
                    </div>
                    <div id="servicos-results" class="results-area">
                        <p class="placeholder">Nenhum resultado ainda.</p>
                    </div>
                </div>
            </div>
        </div>
    `;

    const switchTab = (tab) => {
        document.querySelectorAll('#panel-catalogo .sub-tab').forEach(t => t.classList.remove('active'));
        document.querySelector(`#panel-catalogo .sub-tab[data-subtab="${tab}"]`)?.classList.add('active');
        document.querySelectorAll('#panel-catalogo .sub-panel').forEach(p => p.classList.remove('active'));
        document.getElementById(`subtab-${tab}`)?.classList.add('active');
    };

    const val = (id) => document.getElementById(id)?.value?.trim() || '';

    // ── Renderizadores Peças ──
    const renderPeca = (p) => `
        <div class="result-card">
            <div class="result-header">
                <span class="result-id" onclick="navigator.clipboard.writeText('${p.id}');API.toast('ID copiado!','success')" title="Copiar ID">${p.id}</span>
                <span class="badge badge-${p.ativo !== false ? 'ATIVA' : 'INATIVA'}">${p.ativo !== false ? 'Ativa' : 'Inativa'}</span>
            </div>
            <div class="result-grid">
                <span class="result-label">Nome</span>
                <span class="result-value">${p.nome || '—'}</span>
                <span class="result-label">Código</span>
                <span class="result-value font-mono">${p.codigoFabricante || '—'}</span>
                <span class="result-label">Preço</span>
                <span class="result-value">${API.formatMoney(p.preco)}</span>
                <span class="result-label">Estoque</span>
                <span class="result-value ${(p.quantidade || 0) <= (p.quantidadeMinima || 5) ? 'text-danger' : ''}">${p.quantidade ?? '—'} (mín: ${p.quantidadeMinima ?? 5})</span>
                <span class="result-label">Descrição</span>
                <span class="result-value">${p.descricao || '—'}</span>
            </div>
            <div class="result-actions">
                <button class="btn btn-ghost btn-sm" onclick="CatalogoModule.incrementarEstoque('${p.id}')">📈 +Estoque</button>
                <button class="btn btn-ghost btn-sm" onclick="CatalogoModule.decrementarEstoque('${p.id}')">📉 -Estoque</button>
                <button class="btn btn-warning btn-sm" onclick="CatalogoModule.desativarPeca('${p.id}')">⏸ Desativar</button>
                <button class="btn btn-danger btn-sm" onclick="CatalogoModule.deletarPeca('${p.id}')">🗑 Excluir</button>
            </div>
        </div>
    `;

    const renderServico = (s) => `
        <div class="result-card">
            <div class="result-header">
                <span class="result-id" onclick="navigator.clipboard.writeText('${s.id}');API.toast('ID copiado!','success')" title="Copiar ID">${s.id}</span>
                <span class="badge badge-${s.ativo !== false ? 'ATIVA' : 'INATIVA'}">${s.ativo !== false ? 'Ativo' : 'Inativo'}</span>
            </div>
            <div class="result-grid">
                <span class="result-label">Nome</span>
                <span class="result-value">${s.nome || '—'}</span>
                <span class="result-label">Preço</span>
                <span class="result-value">${API.formatMoney(s.preco)}</span>
                <span class="result-label">Tempo Estimado</span>
                <span class="result-value">${s.tempoEstimadoMinutos ? s.tempoEstimadoMinutos + ' min' : '—'}</span>
                <span class="result-label">Descrição</span>
                <span class="result-value">${s.descricao || '—'}</span>
            </div>
            <div class="result-actions">
                <button class="btn btn-warning btn-sm" onclick="CatalogoModule.desativarServico('${s.id}')">⏸ Desativar</button>
                <button class="btn btn-danger btn-sm" onclick="CatalogoModule.deletarServico('${s.id}')">🗑 Excluir</button>
            </div>
        </div>
    `;

    const renderListPecas = (list) => {
        const items = Array.isArray(list) ? list : (list?.content || []);
        return items.length > 0 ? items.map(renderPeca).join('') : '<p class="placeholder">Nenhuma peça encontrada.</p>';
    };

    const renderListServicos = (list) => {
        const items = Array.isArray(list) ? list : (list?.content || []);
        return items.length > 0 ? items.map(renderServico).join('') : '<p class="placeholder">Nenhum serviço encontrado.</p>';
    };

    // ── Ações de Peças ──
    const criarPeca = async () => {
        const body = {
            nome: val('peca-nome'),
            codigoFabricante: val('peca-codigo'),
            preco: parseFloat(val('peca-preco')) || null,
            quantidade: parseInt(val('peca-qtd')) || null,
            quantidadeMinima: parseInt(val('peca-qtd-min')) || 5,
            descricao: val('peca-desc') || null,
        };
        if (!body.nome || !body.codigoFabricante || !body.preco || !body.quantidade) {
            return API.toast('Preencha nome, código, preço e quantidade.', 'error');
        }
        const urls = API.getUrls();
        const r = await API.http('POST', `${urls.catalog}/api/v1/pecas`, body);
        if (r.ok) {
            API.toast('Peça cadastrada!', 'success');
            listarPecas();
            ['peca-nome', 'peca-codigo', 'peca-preco', 'peca-qtd', 'peca-desc'].forEach(id => {
                const el = document.getElementById(id);
                if (el) el.value = '';
            });
        }
    };

    const buscarPecaPorId = async () => {
        const id = val('peca-searchId');
        if (!id) return API.toast('Informe o ID', 'error');
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.catalog}/api/v1/pecas/${id}`);
        document.getElementById('pecas-results').innerHTML = r.ok ? renderPeca(r.data) : '<p class="placeholder">Não encontrada.</p>';
    };

    const listarPecas = async () => {
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.catalog}/api/v1/pecas`);
        document.getElementById('pecas-results').innerHTML = renderListPecas(r.data);
    };

    const listarPecasAtivas = async () => {
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.catalog}/api/v1/pecas/ativas`);
        document.getElementById('pecas-results').innerHTML = renderListPecas(r.data);
    };

    const buscarPecaCategoria = async () => {
        const cat = val('peca-filterCategoria');
        if (!cat) return API.toast('Informe a categoria', 'error');
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.catalog}/api/v1/pecas/categoria/${encodeURIComponent(cat)}`);
        document.getElementById('pecas-results').innerHTML = renderListPecas(r.data);
    };

    const buscarPecaMarca = async () => {
        const marca = val('peca-filterMarca');
        if (!marca) return API.toast('Informe a marca', 'error');
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.catalog}/api/v1/pecas/marca/${encodeURIComponent(marca)}`);
        document.getElementById('pecas-results').innerHTML = renderListPecas(r.data);
    };

    const incrementarEstoque = async (id) => {
        const qtd = prompt('Quantidade a incrementar:', '5');
        if (!qtd) return;
        const urls = API.getUrls();
        const r = await API.http('PATCH', `${urls.catalog}/api/v1/pecas/${id}/incrementar?quantidade=${qtd}`);
        if (r.ok || r.status === 204) {
            API.toast('Estoque incrementado!', 'success');
            listarPecas();
        }
    };

    const decrementarEstoque = async (id) => {
        const qtd = prompt('Quantidade a decrementar:', '1');
        if (!qtd) return;
        const urls = API.getUrls();
        const r = await API.http('PATCH', `${urls.catalog}/api/v1/pecas/${id}/decrementar?quantidade=${qtd}`);
        if (r.ok || r.status === 204) {
            API.toast('Estoque decrementado!', 'success');
            listarPecas();
        }
    };

    const desativarPeca = async (id) => {
        const urls = API.getUrls();
        const r = await API.http('PATCH', `${urls.catalog}/api/v1/pecas/${id}/desativar`);
        if (r.ok || r.status === 204) {
            API.toast('Peça desativada!', 'success');
            listarPecas();
        }
    };

    const deletarPeca = async (id) => {
        if (!confirm('Excluir esta peça?')) return;
        const urls = API.getUrls();
        const r = await API.http('DELETE', `${urls.catalog}/api/v1/pecas/${id}`);
        if (r.ok || r.status === 204) {
            API.toast('Peça excluída!', 'success');
            listarPecas();
        }
    };

    // ── Ações de Serviços ──
    const criarServico = async () => {
        const body = {
            nome: val('servico-nome'),
            preco: parseFloat(val('servico-preco')) || null,
            tempoEstimadoMinutos: parseInt(val('servico-tempo')) || null,
            descricao: val('servico-desc') || null,
        };
        if (!body.nome || !body.preco || !body.tempoEstimadoMinutos) {
            return API.toast('Preencha nome, preço e tempo estimado.', 'error');
        }
        const urls = API.getUrls();
        const r = await API.http('POST', `${urls.catalog}/api/v1/servicos`, body);
        if (r.ok) {
            API.toast('Serviço cadastrado!', 'success');
            listarServicos();
            ['servico-nome', 'servico-preco', 'servico-tempo', 'servico-desc'].forEach(id => {
                const el = document.getElementById(id);
                if (el) el.value = '';
            });
        }
    };

    const buscarServicoPorId = async () => {
        const id = val('servico-searchId');
        if (!id) return API.toast('Informe o ID', 'error');
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.catalog}/api/v1/servicos/${id}`);
        document.getElementById('servicos-results').innerHTML = r.ok ? renderServico(r.data) : '<p class="placeholder">Não encontrado.</p>';
    };

    const listarServicos = async () => {
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.catalog}/api/v1/servicos`);
        document.getElementById('servicos-results').innerHTML = renderListServicos(r.data);
    };

    const listarServicosAtivos = async () => {
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.catalog}/api/v1/servicos/ativos`);
        document.getElementById('servicos-results').innerHTML = renderListServicos(r.data);
    };

    const buscarServicoCategoria = async () => {
        const cat = val('servico-filterCategoria');
        if (!cat) return API.toast('Informe a categoria', 'error');
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.catalog}/api/v1/servicos/categoria/${encodeURIComponent(cat)}`);
        document.getElementById('servicos-results').innerHTML = renderListServicos(r.data);
    };

    const desativarServico = async (id) => {
        const urls = API.getUrls();
        const r = await API.http('PATCH', `${urls.catalog}/api/v1/servicos/${id}/desativar`);
        if (r.ok || r.status === 204) {
            API.toast('Serviço desativado!', 'success');
            listarServicos();
        }
    };

    const deletarServico = async (id) => {
        if (!confirm('Excluir este serviço?')) return;
        const urls = API.getUrls();
        const r = await API.http('DELETE', `${urls.catalog}/api/v1/servicos/${id}`);
        if (r.ok || r.status === 204) {
            API.toast('Serviço excluído!', 'success');
            listarServicos();
        }
    };

    return {
        render, switchTab,
        criarPeca, buscarPecaPorId, listarPecas, listarPecasAtivas, buscarPecaCategoria, buscarPecaMarca,
        incrementarEstoque, decrementarEstoque, desativarPeca, deletarPeca,
        criarServico, buscarServicoPorId, listarServicos, listarServicosAtivos, buscarServicoCategoria,
        desativarServico, deletarServico,
    };
})();
