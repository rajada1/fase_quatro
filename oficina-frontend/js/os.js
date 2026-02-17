/**
 * ═══════════════════════════════════════════════════════
 *  Oficina Digital — Módulo de Ordens de Serviço
 *  CRUD para /api/v1/ordens-servico (OS Service :8081)
 * ═══════════════════════════════════════════════════════
 */
const OSModule = (() => {
    const STATUS_LIST = [
        'RECEBIDA', 'EM_DIAGNOSTICO', 'AGUARDANDO_APROVACAO',
        'AGUARDANDO_PAGAMENTO', 'EM_EXECUCAO', 'FINALIZADA', 'ENTREGUE', 'CANCELADA'
    ];

    const render = () => `
        <div class="panel-header">
            <h1><span class="accent-os">●</span> Ordens de Serviço</h1>
            <span class="service-tag">os-service :8081</span>
        </div>
        <div class="panel-grid">
            <!-- Criar OS -->
            <div class="card">
                <h3>➕ Criar Ordem de Serviço</h3>
                <div class="form-grid">
                    <div class="form-group">
                        <label>Cliente ID (UUID) *</label>
                        <input type="text" id="os-clienteId" placeholder="UUID do cliente">
                    </div>
                    <div class="form-group">
                        <label>Veículo ID (UUID) *</label>
                        <input type="text" id="os-veiculoId" placeholder="UUID do veículo">
                    </div>
                    <div class="form-group full-width">
                        <label>Descrição do Problema</label>
                        <textarea id="os-descricao" rows="2" placeholder="Descreva o problema do veículo..."></textarea>
                    </div>
                </div>
                <button class="btn btn-primary" onclick="OSModule.criar()">Criar OS</button>
            </div>

            <!-- Buscar / Listar -->
            <div class="card">
                <h3>🔍 Buscar / Listar</h3>
                <div class="form-grid">
                    <div class="form-group">
                        <label>Buscar por ID</label>
                        <input type="text" id="os-searchId" placeholder="UUID da OS">
                    </div>
                    <div class="form-group">
                        <label>Filtrar por Status</label>
                        <select id="os-filterStatus">
                            <option value="">Todos</option>
                            ${STATUS_LIST.map(s => `<option value="${s}">${s.replace(/_/g, ' ')}</option>`).join('')}
                        </select>
                    </div>
                </div>
                <div class="btn-row">
                    <button class="btn btn-secondary" onclick="OSModule.buscarPorId()">Buscar por ID</button>
                    <button class="btn btn-secondary" onclick="OSModule.listarTodas()">Listar Todas</button>
                    <button class="btn btn-secondary" onclick="OSModule.buscarPorStatus()">Filtrar Status</button>
                </div>
            </div>

            <!-- Ações -->
            <div class="card">
                <h3>🎬 Ações de Status</h3>
                <div class="form-grid">
                    <div class="form-group">
                        <label>OS ID</label>
                        <input type="text" id="os-actionId" placeholder="UUID da OS">
                    </div>
                    <div class="form-group">
                        <label>Novo Status</label>
                        <select id="os-novoStatus">
                            ${STATUS_LIST.map(s => `<option value="${s}">${s.replace(/_/g, ' ')}</option>`).join('')}
                        </select>
                    </div>
                </div>
                <button class="btn btn-accent" onclick="OSModule.atualizarStatus()">Atualizar Status</button>
            </div>

            <!-- Resultados -->
            <div class="card full-width">
                <div class="card-header-actions">
                    <h3>📋 Ordens de Serviço</h3>
                    <button class="btn btn-ghost btn-sm" onclick="OSModule.listarTodas()">🔄 Atualizar</button>
                </div>
                <div id="os-results" class="results-area">
                    <p class="placeholder">Nenhum resultado ainda. Use os controles acima.</p>
                </div>
            </div>
        </div>
    `;

    const badge = (status) => `<span class="badge badge-${status || 'UNKNOWN'}">${(status || '—').replace(/_/g, ' ')}</span>`;

    const renderOS = (os) => `
        <div class="result-card">
            <div class="result-header">
                <span class="result-id" onclick="navigator.clipboard.writeText('${os.id}');API.toast('ID copiado!','success')" title="Clique para copiar">${os.id}</span>
                ${badge(os.status)}
            </div>
            <div class="result-grid">
                <span class="result-label">Cliente ID</span>
                <span class="result-value">${os.clienteId || '—'}</span>
                <span class="result-label">Veículo ID</span>
                <span class="result-value">${os.veiculoId || '—'}</span>
                <span class="result-label">Problema</span>
                <span class="result-value">${os.descricaoProblema || '—'}</span>
                <span class="result-label">Criado em</span>
                <span class="result-value">${API.formatDate(os.dataCriacao || os.createdAt)}</span>
            </div>
            <div class="result-actions">
                <button class="btn btn-secondary btn-sm" onclick="OSModule.copiarParaOrcamento('${os.id}')">💵 Orçamento</button>
                <button class="btn btn-secondary btn-sm" onclick="OSModule.copiarParaExecucao('${os.id}')">🔧 Execução</button>
                <button class="btn btn-ghost btn-sm" onclick="document.getElementById('os-actionId').value='${os.id}'">📝 Ações</button>
            </div>
        </div>
    `;

    const renderOSList = (list) => {
        const items = list?.content || list || [];
        if (!Array.isArray(items) || items.length === 0)
            return '<p class="placeholder">Nenhuma OS encontrada.</p>';
        return items.map(renderOS).join('');
    };

    const val = (id) => document.getElementById(id)?.value?.trim() || '';

    // ── Ações ──
    const criar = async () => {
        const urls = API.getUrls();
        const body = {
            clienteId: val('os-clienteId') || API.uuid(),
            veiculoId: val('os-veiculoId') || API.uuid(),
            descricaoProblema: val('os-descricao') || 'Problema genérico para teste',
        };
        const r = await API.http('POST', `${urls.os}/api/v1/ordens-servico`, body);
        if (r.ok) {
            API.toast('OS criada com sucesso!', 'success');
            document.getElementById('os-results').innerHTML = renderOS(r.data);
        }
    };

    const buscarPorId = async () => {
        const id = val('os-searchId');
        if (!id) return API.toast('Informe o ID da OS', 'error');
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.os}/api/v1/ordens-servico/${id}`);
        document.getElementById('os-results').innerHTML = r.ok ? renderOS(r.data) : '<p class="placeholder">Não encontrada.</p>';
    };

    const listarTodas = async () => {
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.os}/api/v1/ordens-servico`);
        document.getElementById('os-results').innerHTML = renderOSList(r.data);
    };

    const buscarPorStatus = async () => {
        const status = val('os-filterStatus');
        if (!status) return API.toast('Selecione um status', 'error');
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.os}/api/v1/ordens-servico/status/${status}`);
        document.getElementById('os-results').innerHTML = renderOSList(r.data);
    };

    const atualizarStatus = async (idOverride, statusOverride) => {
        const id = idOverride || val('os-actionId');
        const novoStatus = statusOverride || val('os-novoStatus');
        if (!id) return API.toast('Informe o ID da OS', 'error');
        const urls = API.getUrls();
        const r = await API.http('PUT', `${urls.os}/api/v1/ordens-servico/${id}/status`, {
            novoStatus,
            observacao: 'Atualização via sistema',
            usuarioAlteracao: 'sistema-frontend',
        });
        if (r.ok) API.toast(`Status atualizado para ${novoStatus}`, 'success');
        return r;
    };

    // ── Integração com outros módulos ──
    const preencherVeiculo = (veiculoId, clienteId) => {
        App.navigateTo('os');
        setTimeout(() => {
            const elV = document.getElementById('os-veiculoId');
            const elC = document.getElementById('os-clienteId');
            if (elV) elV.value = veiculoId;
            if (elC && clienteId) elC.value = clienteId;
        }, 100);
    };

    const copiarParaOrcamento = (osId) => {
        App.navigateTo('billing');
        setTimeout(() => {
            const el = document.getElementById('orc-osId');
            if (el) el.value = osId;
        }, 100);
    };

    const copiarParaExecucao = (osId) => {
        App.navigateTo('execution');
        setTimeout(() => {
            const el = document.getElementById('exec-osId');
            if (el) el.value = osId;
        }, 100);
    };

    return {
        render, criar, buscarPorId, listarTodas, buscarPorStatus,
        atualizarStatus, preencherVeiculo, copiarParaOrcamento, copiarParaExecucao,
    };
})();
