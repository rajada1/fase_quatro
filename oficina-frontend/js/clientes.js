/**
 * ═══════════════════════════════════════════════════════
 *  Oficina Digital — Módulo de Clientes e Veículos
 *  CRUD para /api/v1/clientes e veículos (Customer Service :8084)
 * ═══════════════════════════════════════════════════════
 */
const ClientesModule = (() => {
    let _selectedClienteId = null;

    const render = () => `
        <div class="panel-header">
            <h1><span class="accent-customer">●</span> Clientes & Veículos</h1>
            <span class="service-tag">customer-service :8084</span>
        </div>

        <!-- Sub-tabs -->
        <div class="sub-tabs">
            <button class="sub-tab active" data-subtab="clientes" onclick="ClientesModule.switchTab('clientes')">👤 Clientes</button>
            <button class="sub-tab" data-subtab="veiculos" onclick="ClientesModule.switchTab('veiculos')">🚗 Veículos</button>
        </div>

        <!-- Clientes Sub-Panel -->
        <div class="sub-panel active" id="subtab-clientes">
            <div class="panel-grid">
                <div class="card">
                    <h3>➕ Registrar Cliente</h3>
                    <p class="form-hint">Para criar um cliente, é necessário o ID de uma pessoa já cadastrada.</p>
                    <div class="form-grid">
                        <div class="form-group full-width">
                            <label>Pessoa ID (UUID) *</label>
                            <div class="input-with-action">
                                <input type="text" id="cliente-pessoaId" placeholder="UUID da pessoa cadastrada">
                                <button class="btn btn-ghost btn-sm" onclick="ClientesModule.abrirSeletor()" title="Selecionar pessoa">📋</button>
                            </div>
                        </div>
                    </div>
                    <button class="btn btn-primary" onclick="ClientesModule.criar()">Registrar Cliente</button>
                </div>

                <div class="card">
                    <h3>🔍 Buscar / Listar Clientes</h3>
                    <div class="form-group">
                        <label>Buscar por ID</label>
                        <input type="text" id="cliente-searchId" placeholder="UUID do cliente">
                    </div>
                    <div class="btn-row">
                        <button class="btn btn-secondary" onclick="ClientesModule.buscarPorId()">Buscar por ID</button>
                        <button class="btn btn-secondary" onclick="ClientesModule.listarTodos()">Listar Todos</button>
                    </div>
                </div>

                <div class="card full-width">
                    <div class="card-header-actions">
                        <h3>📋 Clientes</h3>
                        <button class="btn btn-ghost btn-sm" onclick="ClientesModule.listarTodos()">🔄 Atualizar</button>
                    </div>
                    <div id="clientes-results" class="results-area">
                        <p class="placeholder">Nenhum resultado ainda.</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Veículos Sub-Panel -->
        <div class="sub-panel" id="subtab-veiculos">
            <div class="panel-grid">
                <div class="card">
                    <h3>➕ Registrar Veículo</h3>
                    <div class="form-grid">
                        <div class="form-group full-width">
                            <label>Cliente ID (UUID) * — selecione um cliente na listagem</label>
                            <input type="text" id="veiculo-clienteId" placeholder="UUID do cliente (pessoaId)">
                        </div>
                        <div class="form-group">
                            <label>Placa *</label>
                            <input type="text" id="veiculo-placa" placeholder="ABC-1234">
                        </div>
                        <div class="form-group">
                            <label>Marca *</label>
                            <input type="text" id="veiculo-marca" placeholder="Ex: Toyota">
                        </div>
                        <div class="form-group">
                            <label>Modelo *</label>
                            <input type="text" id="veiculo-modelo" placeholder="Ex: Corolla">
                        </div>
                        <div class="form-group">
                            <label>Ano *</label>
                            <input type="number" id="veiculo-ano" placeholder="2024" min="1900" max="2100">
                        </div>
                        <div class="form-group">
                            <label>Cor</label>
                            <input type="text" id="veiculo-cor" placeholder="Prata">
                        </div>
                        <div class="form-group">
                            <label>Renavam</label>
                            <input type="text" id="veiculo-renavam" placeholder="Opcional">
                        </div>
                        <div class="form-group">
                            <label>Chassi</label>
                            <input type="text" id="veiculo-chassi" placeholder="Opcional">
                        </div>
                    </div>
                    <button class="btn btn-primary" onclick="ClientesModule.criarVeiculo()">Registrar Veículo</button>
                </div>

                <div class="card">
                    <h3>🔍 Buscar Veículos</h3>
                    <div class="form-grid">
                        <div class="form-group full-width">
                            <label>Cliente ID para listar veículos</label>
                            <input type="text" id="veiculo-searchClienteId" placeholder="UUID do cliente">
                        </div>
                    </div>
                    <div class="btn-row">
                        <button class="btn btn-secondary" onclick="ClientesModule.listarVeiculos()">Listar Veículos do Cliente</button>
                    </div>
                </div>

                <div class="card full-width">
                    <div class="card-header-actions">
                        <h3>🚗 Veículos</h3>
                    </div>
                    <div id="veiculos-results" class="results-area">
                        <p class="placeholder">Selecione um cliente para ver seus veículos.</p>
                    </div>
                </div>
            </div>
        </div>
    `;

    const switchTab = (tab) => {
        document.querySelectorAll('#panel-clientes .sub-tab').forEach(t => t.classList.remove('active'));
        document.querySelector(`#panel-clientes .sub-tab[data-subtab="${tab}"]`)?.classList.add('active');
        document.querySelectorAll('#panel-clientes .sub-panel').forEach(p => p.classList.remove('active'));
        document.getElementById(`subtab-${tab}`)?.classList.add('active');
    };

    // ── Renderizadores ──
    const renderCliente = (c) => `
        <div class="result-card">
            <div class="result-header">
                <span class="result-id" onclick="navigator.clipboard.writeText('${c.id}');API.toast('ID copiado!','success')" title="Clique para copiar">${c.id}</span>
                <span class="badge badge-CLIENTE">CLIENTE</span>
            </div>
            <div class="result-grid">
                <span class="result-label">Pessoa ID</span>
                <span class="result-value">${c.pessoaId || '—'}</span>
                <span class="result-label">Nome</span>
                <span class="result-value">${c.nome || c.name || '—'}</span>
                <span class="result-label">Email</span>
                <span class="result-value">${c.email || '—'}</span>
                <span class="result-label">Criado em</span>
                <span class="result-value">${API.formatDate(c.createdAt)}</span>
            </div>
            <div class="result-actions">
                <button class="btn btn-secondary btn-sm" onclick="ClientesModule.selecionarCliente('${c.pessoaId || c.id}')">🚗 Ver Veículos</button>
                <button class="btn btn-danger btn-sm" onclick="ClientesModule.deletarCliente('${c.id}')">🗑 Excluir</button>
            </div>
        </div>
    `;

    const renderVeiculo = (v, clienteId) => `
        <div class="result-card">
            <div class="result-header">
                <span class="result-id" onclick="navigator.clipboard.writeText('${v.id}');API.toast('ID copiado!','success')" title="Clique para copiar">${v.id}</span>
                <span class="badge badge-VEICULO">${v.marca} ${v.modelo}</span>
            </div>
            <div class="result-grid">
                <span class="result-label">Placa</span>
                <span class="result-value font-mono">${v.placa || '—'}</span>
                <span class="result-label">Marca/Modelo</span>
                <span class="result-value">${v.marca || ''} ${v.modelo || ''}</span>
                <span class="result-label">Ano</span>
                <span class="result-value">${v.ano || '—'}</span>
                <span class="result-label">Cor</span>
                <span class="result-value">${v.cor || '—'}</span>
                <span class="result-label">Renavam</span>
                <span class="result-value">${v.renavam || '—'}</span>
                <span class="result-label">Chassi</span>
                <span class="result-value">${v.chassi || '—'}</span>
            </div>
            <div class="result-actions">
                <button class="btn btn-secondary btn-sm" onclick="OSModule.preencherVeiculo('${v.id}','${clienteId || ''}')">📋 Criar OS</button>
                <button class="btn btn-danger btn-sm" onclick="ClientesModule.deletarVeiculo('${clienteId || _selectedClienteId}','${v.id}')">🗑 Excluir</button>
            </div>
        </div>
    `;

    const val = (id) => document.getElementById(id)?.value?.trim() || '';

    // ── Ações de Clientes ──
    const criar = async () => {
        const pessoaId = val('cliente-pessoaId');
        if (!pessoaId) return API.toast('Informe o ID da pessoa.', 'error');
        const urls = API.getUrls();
        const r = await API.http('POST', `${urls.customer}/api/v1/clientes`, { pessoaId });
        if (r.ok) {
            API.toast('Cliente registrado!', 'success');
            listarTodos();
        }
    };

    const criarClienteComPessoa = async (pessoaId) => {
        // Navegar para a aba de clientes e preencher
        App.navigateTo('clientes');
        setTimeout(() => {
            const el = document.getElementById('cliente-pessoaId');
            if (el) el.value = pessoaId;
        }, 100);
    };

    const buscarPorId = async () => {
        const id = val('cliente-searchId');
        if (!id) return API.toast('Informe o ID do cliente', 'error');
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.customer}/api/v1/clientes/${id}`);
        document.getElementById('clientes-results').innerHTML = r.ok ? renderCliente(r.data) : '<p class="placeholder">Não encontrado.</p>';
    };

    const listarTodos = async () => {
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.customer}/api/v1/clientes`);
        const items = Array.isArray(r.data) ? r.data : (r.data?.content || []);
        document.getElementById('clientes-results').innerHTML =
            items.length > 0 ? items.map(renderCliente).join('') : '<p class="placeholder">Nenhum cliente encontrado.</p>';
    };

    const deletarCliente = async (id) => {
        if (!confirm('Excluir este cliente?')) return;
        const urls = API.getUrls();
        const r = await API.http('DELETE', `${urls.customer}/api/v1/clientes/${id}`);
        if (r.ok || r.status === 204) {
            API.toast('Cliente excluído!', 'success');
            listarTodos();
        }
    };

    const selecionarCliente = (clienteId) => {
        _selectedClienteId = clienteId;
        switchTab('veiculos');
        document.getElementById('veiculo-clienteId').value = clienteId;
        document.getElementById('veiculo-searchClienteId').value = clienteId;
        listarVeiculos();
    };

    // ── Ações de Veículos ──
    const criarVeiculo = async () => {
        const clienteId = val('veiculo-clienteId');
        if (!clienteId) return API.toast('Informe o ID do cliente.', 'error');
        const body = {
            placa: val('veiculo-placa'),
            marca: val('veiculo-marca'),
            modelo: val('veiculo-modelo'),
            ano: parseInt(val('veiculo-ano')) || null,
            cor: val('veiculo-cor') || null,
            renavam: val('veiculo-renavam') || null,
            chassi: val('veiculo-chassi') || null,
        };
        if (!body.placa || !body.marca || !body.modelo || !body.ano) {
            return API.toast('Preencha placa, marca, modelo e ano.', 'error');
        }
        const urls = API.getUrls();
        const r = await API.http('POST', `${urls.customer}/api/v1/clientes/${clienteId}/veiculos`, body);
        if (r.ok) {
            API.toast('Veículo registrado!', 'success');
            listarVeiculos();
            ['veiculo-placa', 'veiculo-marca', 'veiculo-modelo', 'veiculo-ano', 'veiculo-cor', 'veiculo-renavam', 'veiculo-chassi'].forEach(id => {
                const el = document.getElementById(id);
                if (el) el.value = '';
            });
        }
    };

    const listarVeiculos = async () => {
        const clienteId = val('veiculo-searchClienteId') || _selectedClienteId;
        if (!clienteId) return API.toast('Informe o ID do cliente.', 'error');
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.customer}/api/v1/clientes/${clienteId}/veiculos`);
        const items = Array.isArray(r.data) ? r.data : (r.data?.content || []);
        document.getElementById('veiculos-results').innerHTML =
            items.length > 0 ? items.map(v => renderVeiculo(v, clienteId)).join('') : '<p class="placeholder">Nenhum veículo encontrado para este cliente.</p>';
    };

    const deletarVeiculo = async (clienteId, veiculoId) => {
        if (!confirm('Excluir este veículo?')) return;
        const urls = API.getUrls();
        const r = await API.http('DELETE', `${urls.customer}/api/v1/clientes/${clienteId}/veiculos/${veiculoId}`);
        if (r.ok || r.status === 204) {
            API.toast('Veículo excluído!', 'success');
            listarVeiculos();
        }
    };

    const abrirSeletor = () => {
        API.toast('Vá na aba "Pessoas" e clique em "Criar Cliente" na pessoa desejada.', 'info');
    };

    return {
        render, switchTab, criar, criarClienteComPessoa, buscarPorId, listarTodos,
        deletarCliente, selecionarCliente, criarVeiculo, listarVeiculos,
        deletarVeiculo, abrirSeletor,
    };
})();
