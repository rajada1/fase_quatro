/**
 * ═══════════════════════════════════════════════════════
 *  Oficina Digital — Módulo de Execução
 *  CRUD para /api/v1/execucoes-os (Execution Service :8083)
 * ═══════════════════════════════════════════════════════
 */
const ExecucaoModule = (() => {
    const STATUS_EXEC = ['AGUARDANDO_INICIO', 'EM_ANDAMENTO', 'CONCLUIDA', 'CANCELADA'];

    const render = () => `
        <div class="panel-header">
            <h1><span class="accent-execution">●</span> Execução</h1>
            <span class="service-tag">execution-service :8083</span>
        </div>
        <div class="panel-grid">
            <div class="card">
                <h3>➕ Criar Execução de OS</h3>
                <div class="form-grid">
                    <div class="form-group">
                        <label>OS ID (UUID) *</label>
                        <input type="text" id="exec-osId" placeholder="ID da Ordem de Serviço">
                    </div>
                    <div class="form-group">
                        <label>Orçamento ID (UUID)</label>
                        <input type="text" id="exec-orcamentoId" placeholder="ID do orçamento aprovado">
                    </div>
                    <div class="form-group full-width">
                        <label>Mecânico Responsável *</label>
                        <input type="text" id="exec-mecanico" placeholder="Nome do mecânico">
                    </div>
                </div>
                <button class="btn btn-primary" onclick="ExecucaoModule.criar()">Criar Execução</button>
            </div>

            <div class="card">
                <h3>🔍 Buscar / Filtrar</h3>
                <div class="form-grid">
                    <div class="form-group">
                        <label>Buscar por ID</label>
                        <input type="text" id="exec-searchId" placeholder="UUID da execução">
                    </div>
                    <div class="form-group">
                        <label>Buscar por OS ID</label>
                        <input type="text" id="exec-searchOsId" placeholder="UUID da OS">
                    </div>
                    <div class="form-group">
                        <label>Filtrar por Status</label>
                        <select id="exec-filterStatus">
                            <option value="">Todos</option>
                            ${STATUS_EXEC.map(s => `<option value="${s}">${s.replace(/_/g, ' ')}</option>`).join('')}
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Buscar por Mecânico</label>
                        <input type="text" id="exec-searchMecanico" placeholder="Nome do mecânico">
                    </div>
                </div>
                <div class="btn-row">
                    <button class="btn btn-secondary" onclick="ExecucaoModule.buscarPorId()">Por ID</button>
                    <button class="btn btn-secondary" onclick="ExecucaoModule.buscarPorOsId()">Por OS</button>
                    <button class="btn btn-secondary" onclick="ExecucaoModule.listarTodas()">Listar Todas</button>
                    <button class="btn btn-secondary" onclick="ExecucaoModule.buscarPorStatus()">Por Status</button>
                    <button class="btn btn-secondary" onclick="ExecucaoModule.buscarPorMecanico()">Por Mecânico</button>
                </div>
            </div>

            <div class="card">
                <h3>🎬 Ações de Execução</h3>
                <div class="form-group">
                    <label>Execução ID (UUID)</label>
                    <input type="text" id="exec-actionId" placeholder="UUID da execução">
                </div>
                <div class="form-group">
                    <label>Observações / Motivo</label>
                    <input type="text" id="exec-obs" placeholder="Opcional">
                </div>
                <div class="btn-row">
                    <button class="btn btn-success" onclick="ExecucaoModule.iniciar()">▶️ Iniciar</button>
                    <button class="btn btn-accent" onclick="ExecucaoModule.finalizar()">✅ Finalizar</button>
                    <button class="btn btn-danger" onclick="ExecucaoModule.cancelar()">❌ Cancelar</button>
                    <button class="btn btn-ghost" onclick="ExecucaoModule.deletar()">🗑 Deletar</button>
                </div>
            </div>

            <div class="card full-width">
                <div class="card-header-actions">
                    <h3>🔧 Execuções</h3>
                    <button class="btn btn-ghost btn-sm" onclick="ExecucaoModule.listarTodas()">🔄 Atualizar</button>
                </div>
                <div id="exec-results" class="results-area">
                    <p class="placeholder">Nenhum resultado ainda.</p>
                </div>
            </div>
        </div>
    `;

    const val = (id) => document.getElementById(id)?.value?.trim() || '';

    const badge = (status) => `<span class="badge badge-${status || 'UNKNOWN'}">${(status || '—').replace(/_/g, ' ')}</span>`;

    const renderExecucao = (exec) => {
        if (!exec) return '<p class="placeholder">Nenhum dado.</p>';
        return `
            <div class="result-card">
                <div class="result-header">
                    <span class="result-id" onclick="navigator.clipboard.writeText('${exec.id}');API.toast('ID copiado!','success')" title="Copiar ID">${exec.id}</span>
                    ${badge(exec.status)}
                </div>
                <div class="result-grid">
                    <span class="result-label">OS ID</span>
                    <span class="result-value">${exec.osId || exec.ordemServicoId || '—'}</span>
                    <span class="result-label">Orçamento ID</span>
                    <span class="result-value">${exec.orcamentoId || '—'}</span>
                    <span class="result-label">Mecânico</span>
                    <span class="result-value">${exec.mecanicoResponsavel || exec.mecanico || '—'}</span>
                    <span class="result-label">Início</span>
                    <span class="result-value">${API.formatDate(exec.dataInicio)}</span>
                    <span class="result-label">Fim</span>
                    <span class="result-value">${API.formatDate(exec.dataFim)}</span>
                </div>
                <div class="result-actions">
                    <button class="btn btn-success btn-sm" onclick="document.getElementById('exec-actionId').value='${exec.id}';ExecucaoModule.iniciar()">▶️ Iniciar</button>
                    <button class="btn btn-accent btn-sm" onclick="document.getElementById('exec-actionId').value='${exec.id}';ExecucaoModule.finalizar()">✅ Finalizar</button>
                    <button class="btn btn-danger btn-sm" onclick="document.getElementById('exec-actionId').value='${exec.id}';ExecucaoModule.cancelar()">❌ Cancelar</button>
                </div>
            </div>
        `;
    };

    const renderExecList = (list) => {
        const items = list?.content || list || [];
        if (!Array.isArray(items) || items.length === 0)
            return '<p class="placeholder">Nenhuma execução encontrada.</p>';
        return items.map(renderExecucao).join('');
    };

    // ── Ações ──
    const criar = async () => {
        const urls = API.getUrls();
        const body = {
            osId: val('exec-osId') || API.uuid(),
            orcamentoId: val('exec-orcamentoId') || API.uuid(),
            mecanicoResponsavel: val('exec-mecanico') || 'Mecânico Teste',
        };
        const r = await API.http('POST', `${urls.execution}/api/v1/execucoes-os`, body);
        if (r.ok) API.toast('Execução criada!', 'success');
        document.getElementById('exec-results').innerHTML = renderExecucao(r.data);
        return r;
    };

    const buscarPorId = async () => {
        const id = val('exec-searchId');
        if (!id) return API.toast('Informe o ID da execução', 'error');
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.execution}/api/v1/execucoes-os/${id}`);
        document.getElementById('exec-results').innerHTML = renderExecucao(r.data);
    };

    const buscarPorOsId = async () => {
        const osId = val('exec-searchOsId');
        if (!osId) return API.toast('Informe o OS ID', 'error');
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.execution}/api/v1/execucoes-os/os/${osId}`);
        document.getElementById('exec-results').innerHTML = renderExecList(r.data);
    };

    const listarTodas = async () => {
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.execution}/api/v1/execucoes-os`);
        document.getElementById('exec-results').innerHTML = renderExecList(r.data);
    };

    const buscarPorStatus = async () => {
        const status = val('exec-filterStatus');
        if (!status) return API.toast('Selecione um status', 'error');
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.execution}/api/v1/execucoes-os/status/${status}`);
        document.getElementById('exec-results').innerHTML = renderExecList(r.data);
    };

    const buscarPorMecanico = async () => {
        const nome = val('exec-searchMecanico');
        if (!nome) return API.toast('Informe o nome do mecânico', 'error');
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.execution}/api/v1/execucoes-os/mecanico/${encodeURIComponent(nome)}`);
        document.getElementById('exec-results').innerHTML = renderExecList(r.data);
    };

    const iniciar = async (idOverride) => {
        const id = idOverride || val('exec-actionId');
        if (!id) return API.toast('Informe o ID da execução', 'error');
        const urls = API.getUrls();
        const r = await API.http('PUT', `${urls.execution}/api/v1/execucoes-os/${id}/iniciar`);
        if (r.ok) API.toast('Execução iniciada!', 'success');
        document.getElementById('exec-results').innerHTML = renderExecucao(r.data);
        return r;
    };

    const finalizar = async (idOverride) => {
        const id = idOverride || val('exec-actionId');
        if (!id) return API.toast('Informe o ID da execução', 'error');
        const urls = API.getUrls();
        const obs = val('exec-obs');
        let url = `${urls.execution}/api/v1/execucoes-os/${id}/finalizar`;
        if (obs) url += `?observacoes=${encodeURIComponent(obs)}`;
        const r = await API.http('PUT', url);
        if (r.ok) API.toast('Execução finalizada!', 'success');
        document.getElementById('exec-results').innerHTML = renderExecucao(r.data);
        return r;
    };

    const cancelar = async () => {
        const id = val('exec-actionId');
        if (!id) return API.toast('Informe o ID da execução', 'error');
        const urls = API.getUrls();
        const motivo = val('exec-obs');
        let url = `${urls.execution}/api/v1/execucoes-os/${id}/cancelar`;
        if (motivo) url += `?motivo=${encodeURIComponent(motivo)}`;
        const r = await API.http('PUT', url);
        if (r.ok) API.toast('Execução cancelada.', 'success');
        document.getElementById('exec-results').innerHTML = renderExecucao(r.data);
    };

    const deletar = async () => {
        const id = val('exec-actionId');
        if (!id) return API.toast('Informe o ID da execução', 'error');
        const urls = API.getUrls();
        const r = await API.http('DELETE', `${urls.execution}/api/v1/execucoes-os/${id}`);
        if (r.ok) API.toast('Execução deletada.', 'success');
        document.getElementById('exec-results').innerHTML = '<p class="placeholder">Execução deletada.</p>';
    };

    return {
        render, criar, buscarPorId, buscarPorOsId, listarTodas,
        buscarPorStatus, buscarPorMecanico, iniciar, finalizar, cancelar, deletar,
    };
})();
