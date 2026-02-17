/**
 * ═══════════════════════════════════════════════════════
 *  Oficina Digital — Módulo de Pessoas
 *  CRUD completo para /api/v1/pessoas (People Service :8086)
 * ═══════════════════════════════════════════════════════
 */
const PessoasModule = (() => {
    const TIPOS_PESSOA = ['PESSOA_FISICA', 'PESSOA_JURIDICA'];
    const PERFIS = ['CLIENTE', 'MECANICO', 'ADMIN'];

    const render = () => {
        return `
        <div class="panel-header">
            <h1><span class="accent-people">●</span> Cadastro de Pessoas</h1>
            <span class="service-tag">people-service :8086</span>
        </div>
        <div class="panel-grid">
            <!-- Criar Pessoa -->
            <div class="card">
                <h3>➕ Cadastrar Pessoa</h3>
                <div class="form-grid">
                    <div class="form-group">
                        <label>Nome *</label>
                        <input type="text" id="pessoa-nome" placeholder="Nome completo">
                    </div>
                    <div class="form-group">
                        <label>Email *</label>
                        <input type="email" id="pessoa-email" placeholder="email@exemplo.com">
                    </div>
                    <div class="form-group">
                        <label>Documento (CPF/CNPJ) *</label>
                        <input type="text" id="pessoa-doc" placeholder="12345678901">
                    </div>
                    <div class="form-group">
                        <label>Telefone</label>
                        <input type="text" id="pessoa-telefone" placeholder="11999999999">
                    </div>
                    <div class="form-group">
                        <label>Tipo Pessoa</label>
                        <select id="pessoa-tipo">
                            ${TIPOS_PESSOA.map(t => `<option value="${t}">${t.replace('_', ' ')}</option>`).join('')}
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Perfil</label>
                        <select id="pessoa-perfil">
                            ${PERFIS.map(p => `<option value="${p}">${p}</option>`).join('')}
                        </select>
                    </div>
                </div>
                <button class="btn btn-primary" onclick="PessoasModule.criar()">Cadastrar Pessoa</button>
            </div>

            <!-- Buscar / Listar -->
            <div class="card">
                <h3>🔍 Buscar / Listar</h3>
                <div class="form-grid">
                    <div class="form-group">
                        <label>Buscar por ID</label>
                        <input type="text" id="pessoa-searchId" placeholder="UUID da pessoa">
                    </div>
                    <div class="form-group">
                        <label>Buscar por Documento</label>
                        <input type="text" id="pessoa-searchDoc" placeholder="CPF/CNPJ">
                    </div>
                </div>
                <div class="btn-row">
                    <button class="btn btn-secondary" onclick="PessoasModule.buscarPorId()">Buscar por ID</button>
                    <button class="btn btn-secondary" onclick="PessoasModule.buscarPorDoc()">Buscar por Doc</button>
                    <button class="btn btn-secondary" onclick="PessoasModule.listarTodas()">Listar Todas</button>
                </div>
            </div>

            <!-- Tabela de Resultados -->
            <div class="card full-width">
                <div class="card-header-actions">
                    <h3>📋 Pessoas Cadastradas</h3>
                    <button class="btn btn-ghost btn-sm" onclick="PessoasModule.listarTodas()">🔄 Atualizar</button>
                </div>
                <div id="pessoas-results" class="results-area">
                    <p class="placeholder">Nenhum resultado ainda. Use os controles acima.</p>
                </div>
            </div>
        </div>
        `;
    };

    const renderPessoa = (p) => `
        <div class="result-card">
            <div class="result-header">
                <span class="result-id" onclick="navigator.clipboard.writeText('${p.id}');API.toast('ID copiado!','success')" title="Clique para copiar">${p.id}</span>
                <span class="badge badge-${p.perfil}">${p.perfil || '—'}</span>
            </div>
            <div class="result-grid">
                <span class="result-label">Nome</span>
                <span class="result-value">${p.name || '—'}</span>
                <span class="result-label">Email</span>
                <span class="result-value">${p.email || '—'}</span>
                <span class="result-label">Documento</span>
                <span class="result-value">${p.numeroDocumento || '—'}</span>
                <span class="result-label">Tipo</span>
                <span class="result-value">${(p.tipoPessoa || '—').replace('_', ' ')}</span>
                <span class="result-label">Telefone</span>
                <span class="result-value">${p.phone || '—'}</span>
                <span class="result-label">Criado em</span>
                <span class="result-value">${API.formatDate(p.createdAt)}</span>
            </div>
            <div class="result-actions">
                <button class="btn btn-ghost btn-sm" onclick="PessoasModule.preencherEdicao('${p.id}')">✏️ Editar</button>
                <button class="btn btn-danger btn-sm" onclick="PessoasModule.deletar('${p.id}')">🗑 Excluir</button>
                <button class="btn btn-secondary btn-sm" onclick="ClientesModule.criarClienteComPessoa('${p.id}')">👤 Criar Cliente</button>
            </div>
        </div>
    `;

    const renderList = (list) => {
        const items = Array.isArray(list) ? list : (list?.content || []);
        if (items.length === 0) return '<p class="placeholder">Nenhuma pessoa encontrada.</p>';
        return items.map(renderPessoa).join('');
    };

    const val = (id) => document.getElementById(id)?.value?.trim() || '';

    // ── Ações ──
    const criar = async () => {
        const urls = API.getUrls();
        const body = {
            name: val('pessoa-nome'),
            email: val('pessoa-email'),
            numeroDocumento: val('pessoa-doc'),
            phone: val('pessoa-telefone') || null,
            tipoPessoa: val('pessoa-tipo'),
            perfil: val('pessoa-perfil'),
        };
        if (!body.name || !body.email || !body.numeroDocumento) {
            return API.toast('Preencha nome, email e documento.', 'error');
        }
        const r = await API.http('POST', `${urls.people}/api/v1/pessoas`, body);
        if (r.ok) {
            API.toast('Pessoa cadastrada com sucesso!', 'success');
            listarTodas();
            // Limpar formulário
            ['pessoa-nome', 'pessoa-email', 'pessoa-doc', 'pessoa-telefone'].forEach(id => {
                const el = document.getElementById(id);
                if (el) el.value = '';
            });
        }
    };

    const buscarPorId = async () => {
        const id = val('pessoa-searchId');
        if (!id) return API.toast('Informe o ID da pessoa', 'error');
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.people}/api/v1/pessoas/${id}`);
        document.getElementById('pessoas-results').innerHTML = r.ok ? renderPessoa(r.data) : renderList([]);
    };

    const buscarPorDoc = async () => {
        const doc = val('pessoa-searchDoc');
        if (!doc) return API.toast('Informe o documento', 'error');
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.people}/api/v1/pessoas/documento/${doc}`);
        document.getElementById('pessoas-results').innerHTML = r.ok ? renderPessoa(r.data) : renderList([]);
    };

    const listarTodas = async () => {
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.people}/api/v1/pessoas`);
        document.getElementById('pessoas-results').innerHTML = renderList(r.data);
    };

    const preencherEdicao = async (id) => {
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.people}/api/v1/pessoas/${id}`);
        if (!r.ok) return;
        const p = r.data;
        document.getElementById('pessoa-nome').value = p.name || '';
        document.getElementById('pessoa-email').value = p.email || '';
        document.getElementById('pessoa-doc').value = p.numeroDocumento || '';
        document.getElementById('pessoa-telefone').value = p.phone || '';
        document.getElementById('pessoa-tipo').value = p.tipoPessoa || 'PESSOA_FISICA';
        document.getElementById('pessoa-perfil').value = p.perfil || 'CLIENTE';
        // Trocar botão de criar para atualizar
        const btnContainer = document.querySelector('#panel-pessoas .card .btn-primary');
        if (btnContainer) {
            btnContainer.textContent = '💾 Atualizar Pessoa';
            btnContainer.onclick = () => atualizar(id);
        }
        API.toast('Dados carregados para edição.', 'info');
    };

    const atualizar = async (id) => {
        const urls = API.getUrls();
        const body = {
            name: val('pessoa-nome'),
            email: val('pessoa-email'),
            numeroDocumento: val('pessoa-doc'),
            phone: val('pessoa-telefone') || null,
            tipoPessoa: val('pessoa-tipo'),
            perfil: val('pessoa-perfil'),
        };
        const r = await API.http('PUT', `${urls.people}/api/v1/pessoas/${id}`, body);
        if (r.ok) {
            API.toast('Pessoa atualizada com sucesso!', 'success');
            listarTodas();
            // Restaurar botão de criar
            const btnContainer = document.querySelector('#panel-pessoas .card .btn-primary');
            if (btnContainer) {
                btnContainer.textContent = 'Cadastrar Pessoa';
                btnContainer.onclick = criar;
            }
        }
    };

    const deletar = async (id) => {
        if (!confirm('Tem certeza que deseja excluir esta pessoa?')) return;
        const urls = API.getUrls();
        const r = await API.http('DELETE', `${urls.people}/api/v1/pessoas/${id}`);
        if (r.ok || r.status === 204) {
            API.toast('Pessoa excluída com sucesso!', 'success');
            listarTodas();
        }
    };

    return { render, criar, buscarPorId, buscarPorDoc, listarTodas, preencherEdicao, atualizar, deletar };
})();
