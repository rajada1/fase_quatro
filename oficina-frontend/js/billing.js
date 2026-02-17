/**
 * ═══════════════════════════════════════════════════════
 *  Oficina Digital — Módulo de Faturamento
 *  Orçamentos e Pagamentos (Billing Service :8082)
 * ═══════════════════════════════════════════════════════
 */
const BillingModule = (() => {
    let _lastOrcId = null;
    let _lastPagId = null;

    const render = () => `
        <div class="panel-header">
            <h1><span class="accent-billing">●</span> Faturamento</h1>
            <span class="service-tag">billing-service :8082</span>
        </div>

        <div class="sub-tabs">
            <button class="sub-tab active" data-subtab="orcamentos" onclick="BillingModule.switchTab('orcamentos')">💵 Orçamentos</button>
            <button class="sub-tab" data-subtab="pagamentos" onclick="BillingModule.switchTab('pagamentos')">💳 Pagamentos</button>
        </div>

        <!-- Orçamentos -->
        <div class="sub-panel active" id="subtab-orcamentos">
            <div class="panel-grid">
                <div class="card">
                    <h3>➕ Criar Orçamento</h3>
                    <div class="form-grid">
                        <div class="form-group">
                            <label>OS ID (UUID) *</label>
                            <input type="text" id="orc-osId" placeholder="ID da Ordem de Serviço">
                        </div>
                        <div class="form-group">
                            <label>Observação</label>
                            <input type="text" id="orc-observacao" placeholder="Opcional">
                        </div>
                    </div>
                    <div class="items-section">
                        <h4>Itens do Orçamento</h4>
                        <div id="orc-items">
                            <div class="item-row">
                                <input type="text" placeholder="Descrição" class="item-desc">
                                <input type="number" placeholder="Valor" class="item-valor" step="0.01">
                                <input type="number" placeholder="Qtd" class="item-qty" value="1" min="1">
                                <button class="btn-icon btn-remove" onclick="this.parentElement.remove()">✕</button>
                            </div>
                        </div>
                        <button class="btn btn-ghost" onclick="BillingModule.addItem()">+ Adicionar Item</button>
                    </div>
                    <button class="btn btn-primary" onclick="BillingModule.criarOrcamento()">Criar Orçamento</button>
                </div>

                <div class="card">
                    <h3>🔍 Buscar Orçamento</h3>
                    <div class="form-group">
                        <label>Orçamento ID (UUID)</label>
                        <input type="text" id="orc-searchId" placeholder="UUID do orçamento">
                    </div>
                    <div class="btn-row">
                        <button class="btn btn-secondary" onclick="BillingModule.buscarOrcamento()">Buscar</button>
                        <button class="btn btn-accent" onclick="BillingModule.listarOrcamentos()">📋 Listar Todos</button>
                    </div>
                    <div class="action-buttons" id="orc-actions" style="display:none;">
                        <button class="btn btn-success" onclick="BillingModule.aprovarOrcamento()">✅ Aprovar</button>
                        <button class="btn btn-warning" onclick="BillingModule.rejeitarOrcamento()">❌ Rejeitar</button>
                        <button class="btn btn-danger" onclick="BillingModule.cancelarOrcamento()">🗑 Cancelar</button>
                    </div>
                </div>

                <div class="card full-width">
                    <h3>📊 Resultado</h3>
                    <div id="orc-results" class="results-area">
                        <p class="placeholder">Nenhum resultado ainda.</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Pagamentos -->
        <div class="sub-panel" id="subtab-pagamentos">
            <div class="panel-grid">
                <div class="card">
                    <h3>➕ Registrar Pagamento (Gerar Link)</h3>
                    <p style="font-size:0.85em; color:#aaa; margin-bottom:12px;">
                        Ao registrar, será gerado um <strong>link de pagamento</strong> do Mercado Pago para enviar ao cliente.
                    </p>
                    <div class="form-grid">
                        <div class="form-group">
                            <label>Orçamento ID *</label>
                            <input type="text" id="pag-orcamentoId" placeholder="UUID do orçamento">
                        </div>
                        <div class="form-group">
                            <label>OS ID *</label>
                            <input type="text" id="pag-osId" placeholder="UUID da OS">
                        </div>
                        <div class="form-group">
                            <label>Valor (R$) *</label>
                            <input type="number" id="pag-valor" placeholder="0.00" step="0.01">
                        </div>
                        <div class="form-group">
                            <label>Forma de Pagamento</label>
                            <select id="pag-forma">
                                <option value="PIX">PIX</option>
                                <option value="CARTAO_CREDITO">Cartão de Crédito</option>
                                <option value="CARTAO_DEBITO">Cartão de Débito</option>
                                <option value="BOLETO">Boleto</option>
                                <option value="DINHEIRO">Dinheiro</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>Email do Pagador</label>
                            <input type="email" id="pag-email" placeholder="payer@email.com">
                        </div>
                    </div>
                    <button class="btn btn-primary" onclick="BillingModule.registrarPagamento()">💳 Gerar Link de Pagamento</button>
                </div>

                <div class="card">
                    <h3>🔍 Checar Pagamento</h3>
                    <p style="font-size:0.85em; color:#aaa; margin-bottom:12px;">
                        Verifica no Mercado Pago se o cliente já pagou.
                    </p>
                    <div class="form-group">
                        <label>Pagamento ID (UUID)</label>
                        <input type="text" id="pag-checarId" placeholder="UUID do pagamento">
                    </div>
                    <div class="btn-row">
                        <button class="btn btn-accent" onclick="BillingModule.checarPagamento()">🔄 Checar Pagamento no MP</button>
                        <button class="btn btn-secondary" onclick="BillingModule.listarPagamentos()">📋 Listar Todos</button>
                    </div>
                </div>

                <div class="card">
                    <h3>🔧 Ações Manuais</h3>
                    <div class="form-group">
                        <label>Pagamento ID (UUID)</label>
                        <input type="text" id="pag-actionId" placeholder="UUID do pagamento">
                    </div>
                    <div class="form-group">
                        <label>Motivo (para estorno)</label>
                        <input type="text" id="pag-motivo" placeholder="Opcional">
                    </div>
                    <div class="btn-row">
                        <button class="btn btn-success" onclick="BillingModule.confirmarPagamento()">✅ Confirmar</button>
                        <button class="btn btn-warning" onclick="BillingModule.estornarPagamento()">↩️ Estornar</button>
                        <button class="btn btn-danger" onclick="BillingModule.cancelarPagamento()">🗑 Cancelar</button>
                    </div>
                </div>

                <div class="card full-width">
                    <h3>📊 Resultado</h3>
                    <div id="pag-results" class="results-area">
                        <p class="placeholder">Nenhum resultado ainda.</p>
                    </div>
                </div>
            </div>
        </div>
    `;

    const switchTab = (tab) => {
        document.querySelectorAll('#panel-billing .sub-tab').forEach(t => t.classList.remove('active'));
        document.querySelector(`#panel-billing .sub-tab[data-subtab="${tab}"]`)?.classList.add('active');
        document.querySelectorAll('#panel-billing .sub-panel').forEach(p => p.classList.remove('active'));
        document.getElementById(`subtab-${tab}`)?.classList.add('active');
    };

    const val = (id) => document.getElementById(id)?.value?.trim() || '';

    // ── Renderizadores ──
    const renderOrcamento = (orc) => {
        if (!orc) return '<p class="placeholder">Nenhum dado.</p>';
        const orcStatusStr = String(orc.status || 'UNKNOWN');
        const itensHtml = (orc.itens || [])
            .map(i => `<span class="result-value">• ${i.descricao} — ${API.formatMoney(i.valor)} x${i.quantidade || 1}</span>`)
            .join('');
        return `
            <div class="result-card">
                <div class="result-header">
                    <span class="result-id" onclick="navigator.clipboard.writeText('${orc.id}');API.toast('ID copiado!','success')" title="Copiar ID">${orc.id}</span>
                    <span class="badge badge-${orcStatusStr}">${orcStatusStr.replace(/_/g, ' ')}</span>
                </div>
                <div class="result-grid">
                    <span class="result-label">OS ID</span>
                    <span class="result-value">${orc.osId || '—'}</span>
                    <span class="result-label">Valor Total</span>
                    <span class="result-value font-bold">${API.formatMoney(orc.valorTotal)}</span>
                    <span class="result-label">Observação</span>
                    <span class="result-value">${orc.observacao || '—'}</span>
                    <span class="result-label">Criado em</span>
                    <span class="result-value">${API.formatDate(orc.dataCriacao || orc.createdAt)}</span>
                </div>
                ${itensHtml ? `<div class="result-items">${itensHtml}</div>` : ''}
                <div class="result-actions">
                    <button class="btn btn-success btn-sm" onclick="BillingModule.aprovarOrcamentoPorId('${orc.id}')">✅ Aprovar</button>
                    <button class="btn btn-secondary btn-sm" onclick="BillingModule.criarPagamentoPara('${orc.id}','${orc.osId}','${orc.valorTotal}')">💳 Pagar</button>
                </div>
            </div>
        `;
    };

    const renderPagamento = (pag) => {
        if (!pag) return '<p class="placeholder">Nenhum dado.</p>';

        const hasLink = pag.initPoint;
        const statusEmoji = {
            'PENDENTE': '⏳',
            'PROCESSANDO': '🔄',
            'CONFIRMADO': '✅',
            'ESTORNADO': '↩️',
            'CANCELADO': '❌',
        };
        const statusStr = String(pag.status || 'UNKNOWN');
        const emoji = statusEmoji[statusStr] || '❓';
        const formaStr = String(pag.formaPagamento || '—');

        return `
            <div class="result-card">
                <div class="result-header">
                    <span class="result-id" onclick="navigator.clipboard.writeText('${pag.id}');API.toast('ID copiado!','success')" title="Copiar ID">${pag.id}</span>
                    <span class="badge badge-${statusStr}">${emoji} ${statusStr.replace(/_/g, ' ')}</span>
                </div>
                <div class="result-grid">
                    <span class="result-label">Orçamento ID</span>
                    <span class="result-value">${pag.orcamentoId || '—'}</span>
                    <span class="result-label">OS ID</span>
                    <span class="result-value">${pag.osId || '—'}</span>
                    <span class="result-label">Valor</span>
                    <span class="result-value font-bold">${API.formatMoney(pag.valor)}</span>
                    <span class="result-label">Forma</span>
                    <span class="result-value">${formaStr.replace(/_/g, ' ')}</span>
                    <span class="result-label">Preference ID</span>
                    <span class="result-value">${pag.mercadoPagoPreferenceId || '—'}</span>
                    <span class="result-label">MP Payment ID</span>
                    <span class="result-value">${pag.mercadoPagoPaymentId || '—'}</span>
                    <span class="result-label">Criado em</span>
                    <span class="result-value">${API.formatDate(pag.dataCriacao || pag.createdAt)}</span>
                    ${pag.dataPagamento ? `
                        <span class="result-label">Pago em</span>
                        <span class="result-value">${API.formatDate(pag.dataPagamento)}</span>
                    ` : ''}
                </div>
                ${hasLink ? `
                    <div style="margin-top:12px; padding:12px; background:rgba(0,122,255,0.1); border-radius:8px; border:1px solid rgba(0,122,255,0.3);">
                        <strong style="color:#007aff;">🔗 Link de Pagamento:</strong><br>
                        <a href="${pag.initPoint}" target="_blank" rel="noopener" style="color:#00d4ff; word-break:break-all; font-size:0.85em;">${pag.initPoint}</a>
                        <div style="margin-top:8px;">
                            <button class="btn btn-accent btn-sm" onclick="navigator.clipboard.writeText('${pag.initPoint}');API.toast('Link copiado!','success')">📋 Copiar Link</button>
                            <button class="btn btn-primary btn-sm" onclick="window.open('${pag.initPoint}','_blank')">🌐 Abrir Link</button>
                        </div>
                    </div>
                ` : ''}
                <div class="result-actions" style="margin-top:12px;">
                    <button class="btn btn-accent btn-sm" onclick="BillingModule.checarPagamentoPorId('${pag.id}')">🔄 Checar Pagamento</button>
                    ${pag.status === 'PENDENTE' || pag.status === 'PROCESSANDO' ? `
                        <button class="btn btn-success btn-sm" onclick="BillingModule.confirmarPagamentoPorId('${pag.id}')">✅ Confirmar Manual</button>
                    ` : ''}
                </div>
            </div>
        `;
    };

    // ── Ações de Orçamento ──
    const addItem = () => {
        const container = document.getElementById('orc-items');
        const row = document.createElement('div');
        row.className = 'item-row';
        row.innerHTML = `
            <input type="text" placeholder="Descrição" class="item-desc">
            <input type="number" placeholder="Valor" class="item-valor" step="0.01">
            <input type="number" placeholder="Qtd" class="item-qty" value="1" min="1">
            <button class="btn-icon btn-remove" onclick="this.parentElement.remove()">✕</button>
        `;
        container.appendChild(row);
    };

    const criarOrcamento = async () => {
        const urls = API.getUrls();
        const rows = document.querySelectorAll('#orc-items .item-row');
        const itens = [];
        rows.forEach(row => {
            const desc = row.querySelector('.item-desc').value.trim();
            const valor = parseFloat(row.querySelector('.item-valor').value) || 0;
            const qty = parseInt(row.querySelector('.item-qty').value) || 1;
            if (desc && valor > 0) itens.push({ descricao: desc, valor, quantidade: qty });
        });
        if (itens.length === 0) {
            itens.push({ descricao: 'Serviço de teste', valor: 150.00, quantidade: 1 });
        }
        const body = {
            osId: val('orc-osId') || API.uuid(),
            itens,
            observacao: val('orc-observacao') || null,
        };
        const r = await API.http('POST', `${urls.billing}/api/v1/orcamentos`, body);
        if (r.ok) API.toast('Orçamento criado!', 'success');
        document.getElementById('orc-results').innerHTML = renderOrcamento(r.data);
    };

    const buscarOrcamento = async () => {
        const id = val('orc-searchId');
        if (!id) return API.toast('Informe o ID do orçamento', 'error');
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.billing}/api/v1/orcamentos/${id}`);
        document.getElementById('orc-results').innerHTML = renderOrcamento(r.data);
        if (r.ok) {
            _lastOrcId = id;
            const actions = document.getElementById('orc-actions');
            if (actions) actions.style.display = 'flex';
        }
    };

    const aprovarOrcamento = async () => {
        const id = val('orc-searchId') || _lastOrcId;
        if (!id) return API.toast('Busque um orçamento primeiro', 'error');
        return aprovarOrcamentoPorId(id);
    };

    const aprovarOrcamentoPorId = async (id) => {
        const urls = API.getUrls();
        const r = await API.http('PUT', `${urls.billing}/api/v1/orcamentos/${id}/aprovar`);
        if (r.ok) API.toast('Orçamento aprovado!', 'success');
        document.getElementById('orc-results').innerHTML = renderOrcamento(r.data);
        return r;
    };

    const rejeitarOrcamento = async () => {
        const id = val('orc-searchId') || _lastOrcId;
        if (!id) return API.toast('Busque um orçamento primeiro', 'error');
        const urls = API.getUrls();
        const motivo = prompt('Motivo da rejeição (opcional):') || '';
        const r = await API.http('PUT', `${urls.billing}/api/v1/orcamentos/${id}/rejeitar?motivo=${encodeURIComponent(motivo)}`);
        if (r.ok) API.toast('Orçamento rejeitado.', 'success');
        document.getElementById('orc-results').innerHTML = renderOrcamento(r.data);
    };

    const cancelarOrcamento = async () => {
        const id = val('orc-searchId') || _lastOrcId;
        if (!id) return API.toast('Busque um orçamento primeiro', 'error');
        const urls = API.getUrls();
        const r = await API.http('DELETE', `${urls.billing}/api/v1/orcamentos/${id}`);
        if (r.ok) API.toast('Orçamento cancelado.', 'success');
        document.getElementById('orc-results').innerHTML = '<p class="placeholder">Orçamento cancelado.</p>';
    };

    // ── Ações de Pagamento ──
    const registrarPagamento = async () => {
        const urls = API.getUrls();
        const body = {
            orcamentoId: val('pag-orcamentoId') || API.uuid(),
            osId: val('pag-osId') || API.uuid(),
            valor: parseFloat(val('pag-valor')) || 100.00,
            formaPagamento: val('pag-forma') || 'PIX',
            comprovante: null,
            payerEmail: val('pag-email') || 'test@example.com',
        };
        const r = await API.http('POST', `${urls.billing}/api/v1/pagamentos`, body);
        if (r.ok) {
            API.toast('Link de pagamento gerado!', 'success');
            _lastPagId = r.data?.id;
            // Auto-preencher campo de checagem
            const checarEl = document.getElementById('pag-checarId');
            if (checarEl && r.data?.id) checarEl.value = r.data.id;
            const actionEl = document.getElementById('pag-actionId');
            if (actionEl && r.data?.id) actionEl.value = r.data.id;
            document.getElementById('pag-results').innerHTML = renderPagamento(r.data);
        } else {
            const errMsg = r.data?.message || r.data?.error || `Erro ${r.status}`;
            document.getElementById('pag-results').innerHTML = `<div class="result-card"><p style="color:var(--error)">❌ Falha ao gerar link de pagamento: ${errMsg}</p></div>`;
        }
        return r;
    };

    const checarPagamento = async () => {
        const id = val('pag-checarId') || _lastPagId;
        if (!id) return API.toast('Informe o ID do pagamento', 'error');
        return checarPagamentoPorId(id);
    };

    const checarPagamentoPorId = async (id) => {
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.billing}/api/v1/pagamentos/${id}/checar`);
        if (r.ok) {
            const status = r.data?.status;
            if (status === 'CONFIRMADO') {
                API.toast('✅ Pagamento CONFIRMADO!', 'success');
            } else if (status === 'PROCESSANDO') {
                API.toast('🔄 Pagamento em processamento...', 'info');
            } else if (status === 'PENDENTE') {
                API.toast('⏳ Cliente ainda não pagou.', 'info');
            } else if (status === 'CANCELADO') {
                API.toast('❌ Pagamento foi cancelado/rejeitado.', 'error');
            } else if (status === 'ESTORNADO') {
                API.toast('↩️ Pagamento foi estornado.', 'warning');
            } else {
                API.toast(`Status: ${status}`, 'info');
            }
        }
        document.getElementById('pag-results').innerHTML = r.ok
            ? renderPagamento(r.data)
            : `<div class="result-card"><p style="color:var(--error)">❌ Erro ao checar pagamento: ${r.data?.message || r.data?.error || 'Erro ' + r.status}</p></div>`;
        return r;
    };

    const confirmarPagamento = async () => {
        const id = val('pag-actionId');
        if (!id) return API.toast('Informe o ID do pagamento', 'error');
        return confirmarPagamentoPorId(id);
    };

    const confirmarPagamentoPorId = async (id) => {
        const urls = API.getUrls();
        const r = await API.http('PUT', `${urls.billing}/api/v1/pagamentos/${id}/confirmar`);
        if (r.ok) API.toast('Pagamento confirmado!', 'success');
        document.getElementById('pag-results').innerHTML = r.ok
            ? renderPagamento(r.data)
            : `<div class="result-card"><p style="color:var(--error)">❌ Erro ao confirmar pagamento: ${r.data?.message || r.data?.error || 'Erro ' + r.status}</p></div>`;
        return r;
    };

    const estornarPagamento = async () => {
        const id = val('pag-actionId');
        if (!id) return API.toast('Informe o ID do pagamento', 'error');
        const urls = API.getUrls();
        const motivo = val('pag-motivo');
        let url = `${urls.billing}/api/v1/pagamentos/${id}/estornar`;
        if (motivo) url += `?motivo=${encodeURIComponent(motivo)}`;
        const r = await API.http('PUT', url);
        if (r.ok) API.toast('Pagamento estornado.', 'success');
        document.getElementById('pag-results').innerHTML = r.ok
            ? renderPagamento(r.data)
            : `<div class="result-card"><p style="color:var(--error)">❌ Erro ao estornar pagamento: ${r.data?.message || r.data?.error || 'Erro ' + r.status}</p></div>`;
    };

    const cancelarPagamento = async () => {
        const id = val('pag-actionId');
        if (!id) return API.toast('Informe o ID do pagamento', 'error');
        const urls = API.getUrls();
        const r = await API.http('DELETE', `${urls.billing}/api/v1/pagamentos/${id}`);
        if (r.ok) API.toast('Pagamento cancelado.', 'success');
        document.getElementById('pag-results').innerHTML = '<p class="placeholder">Pagamento cancelado.</p>';
    };

    // ── Listar Orçamentos ──
    const listarOrcamentos = async () => {
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.billing}/api/v1/orcamentos`);
        if (r.ok) {
            const lista = Array.isArray(r.data) ? r.data : [];
            if (lista.length === 0) {
                document.getElementById('orc-results').innerHTML = '<p class="placeholder">Nenhum orçamento encontrado.</p>';
            } else {
                API.toast(`${lista.length} orçamento(s) encontrado(s)`, 'success');
                document.getElementById('orc-results').innerHTML = lista.map(renderOrcamento).join('');
            }
        } else {
            const errMsg = r.data?.message || r.data?.error || `Erro ${r.status}`;
            document.getElementById('orc-results').innerHTML = `<div class="result-card"><p style="color:var(--error)">❌ Erro ao listar orçamentos: ${errMsg}</p></div>`;
        }
    };

    // ── Listar Pagamentos ──
    const listarPagamentos = async () => {
        const urls = API.getUrls();
        const r = await API.http('GET', `${urls.billing}/api/v1/pagamentos`);
        if (r.ok) {
            const lista = Array.isArray(r.data) ? r.data : [];
            if (lista.length === 0) {
                document.getElementById('pag-results').innerHTML = '<p class="placeholder">Nenhum pagamento encontrado.</p>';
            } else {
                API.toast(`${lista.length} pagamento(s) encontrado(s)`, 'success');
                document.getElementById('pag-results').innerHTML = lista.map(renderPagamento).join('');
            }
        } else {
            const errMsg = r.data?.message || r.data?.error || `Erro ${r.status}`;
            document.getElementById('pag-results').innerHTML = `<div class="result-card"><p style="color:var(--error)">❌ Erro ao listar pagamentos: ${errMsg}</p></div>`;
        }
    };

    // ── Integração ──
    const criarPagamentoPara = (orcId, osId, valor) => {
        switchTab('pagamentos');
        setTimeout(() => {
            const elO = document.getElementById('pag-orcamentoId');
            const elS = document.getElementById('pag-osId');
            const elV = document.getElementById('pag-valor');
            if (elO) elO.value = orcId;
            if (elS && osId !== 'undefined') elS.value = osId;
            if (elV && valor !== 'undefined' && valor !== 'null') elV.value = valor;
        }, 100);
    };

    return {
        render, switchTab, addItem,
        criarOrcamento, buscarOrcamento, listarOrcamentos, aprovarOrcamento, aprovarOrcamentoPorId,
        rejeitarOrcamento, cancelarOrcamento,
        registrarPagamento, checarPagamento, checarPagamentoPorId, listarPagamentos,
        confirmarPagamento, confirmarPagamentoPorId,
        estornarPagamento, cancelarPagamento,
        criarPagamentoPara,
    };
})();
