/**
 * ═══════════════════════════════════════════════════════
 *  Oficina Digital — App Principal (Orquestrador)
 *  Gerencia navegação, configurações e inicialização
 * ═══════════════════════════════════════════════════════
 */
const App = (() => {
    const PANELS = {
        pessoas:   { module: () => PessoasModule,   icon: '👥', label: 'Pessoas',         badge: 'PPL' },
        clientes:  { module: () => ClientesModule,  icon: '👤', label: 'Clientes & Veículos', badge: 'CLI' },
        catalogo:  { module: () => CatalogoModule,  icon: '🔩', label: 'Catálogo',         badge: 'CAT' },
        os:        { module: () => OSModule,         icon: '📋', label: 'Ordens de Serviço', badge: 'OS' },
        billing:   { module: () => BillingModule,    icon: '💰', label: 'Faturamento',      badge: 'BIL' },
        execution: { module: () => ExecucaoModule,   icon: '🔧', label: 'Execução',         badge: 'EXE' },
        saga:      { module: () => SagaModule,       icon: '🔄', label: 'Saga Flow',        badge: null },
        auth:      { module: null,                  icon: '🔐', label: 'Autenticação',      badge: null },
        settings:  { module: null,                  icon: '⚡', label: 'API Config',        badge: null },
    };

    let currentPanel = 'os';

    const navigateTo = (panel) => {
        if (!PANELS[panel]) return;
        currentPanel = panel;

        // Atualizar navegação
        document.querySelectorAll('.nav-item').forEach(b => b.classList.remove('active'));
        const navBtn = document.querySelector(`.nav-item[data-panel="${panel}"]`);
        if (navBtn) navBtn.classList.add('active');

        // Renderizar painel
        const contentArea = document.getElementById('contentArea');
        if (panel === 'settings') {
            contentArea.innerHTML = renderSettings();
            initSettings();
        } else if (panel === 'auth') {
            contentArea.innerHTML = AuthModule.render();
        } else {
            const mod = PANELS[panel].module();
            contentArea.innerHTML = `<section class="panel active" id="panel-${panel}">${mod.render()}</section>`;
        }
    };

    // ── Settings Panel ──
    const renderSettings = () => `
        <section class="panel active" id="panel-settings">
            <div class="panel-header">
                <h1><span class="accent-settings">●</span> API Configuration</h1>
                <span class="service-tag">settings</span>
            </div>
            <div class="panel-grid">
                <div class="card full-width">
                    <h3>🔌 Modo de Conexão</h3>
                    <p class="form-hint">Use <strong>Gateway (porta 8080)</strong> para evitar problemas de CORS. Use <strong>Direto</strong> apenas se os serviços tiverem CORS configurado.</p>
                    <div class="btn-row" style="margin-bottom:16px">
                        <button class="btn btn-primary" onclick="App.useGateway()">🔒 Usar Gateway (:8080) — Recomendado</button>
                        <button class="btn btn-secondary" onclick="App.useDirect()">🔓 Usar Portas Diretas</button>
                    </div>
                </div>
                <div class="card">
                    <h3>🌐 URLs dos Serviços</h3>
                    <div class="form-grid">
                        <div class="form-group">
                            <label>OS Service (:8081)</label>
                            <input type="text" id="cfg-os-url" placeholder="http://localhost:8080">
                        </div>
                        <div class="form-group">
                            <label>Billing Service (:8082)</label>
                            <input type="text" id="cfg-billing-url" placeholder="http://localhost:8080">
                        </div>
                        <div class="form-group">
                            <label>Execution Service (:8083)</label>
                            <input type="text" id="cfg-execution-url" placeholder="http://localhost:8080">
                        </div>
                        <div class="form-group">
                            <label>Customer Service (:8084)</label>
                            <input type="text" id="cfg-customer-url" placeholder="http://localhost:8080">
                        </div>
                        <div class="form-group">
                            <label>Catalog Service (:8085)</label>
                            <input type="text" id="cfg-catalog-url" placeholder="http://localhost:8080">
                        </div>
                        <div class="form-group">
                            <label>People Service (:8086)</label>
                            <input type="text" id="cfg-people-url" placeholder="http://localhost:8080">
                        </div>
                    </div>
                    <div class="btn-row">
                        <button class="btn btn-primary" onclick="App.saveSettings()">💾 Salvar</button>
                        <button class="btn btn-ghost" onclick="App.resetSettings()">🔄 Resetar para Gateway</button>
                    </div>
                </div>
                <div class="card">
                    <h3>🏥 Health Check</h3>
                    <div id="health-results" class="health-grid">
                        <div class="health-item" id="health-os"><span class="health-dot">●</span><span>OS Service</span><span class="health-status">—</span></div>
                        <div class="health-item" id="health-billing"><span class="health-dot">●</span><span>Billing Service</span><span class="health-status">—</span></div>
                        <div class="health-item" id="health-execution"><span class="health-dot">●</span><span>Execution Service</span><span class="health-status">—</span></div>
                        <div class="health-item" id="health-customer"><span class="health-dot">●</span><span>Customer Service</span><span class="health-status">—</span></div>
                        <div class="health-item" id="health-catalog"><span class="health-dot">●</span><span>Catalog Service</span><span class="health-status">—</span></div>
                        <div class="health-item" id="health-people"><span class="health-dot">●</span><span>People Service</span><span class="health-status">—</span></div>
                    </div>
                    <button class="btn btn-secondary" onclick="App.healthCheck()">🔍 Verificar Saúde</button>
                </div>
            </div>
        </section>
    `;

    const initSettings = () => {
        const urls = API.getUrls();
        Object.keys(urls).forEach(key => {
            const el = document.getElementById(`cfg-${key}-url`);
            if (el) el.value = urls[key];
        });
    };

    const saveSettings = () => {
        const urls = {};
        Object.keys(API.DEFAULT_URLS).forEach(key => {
            const el = document.getElementById(`cfg-${key}-url`);
            urls[key] = el?.value?.trim() || API.DEFAULT_URLS[key];
        });
        API.setUrls(urls);
        API.toast('Configurações salvas!', 'success');
    };

    const resetSettings = () => {
        API.resetUrls();
        initSettings();
        API.toast('URLs resetadas para padrão.', 'info');
    };

    const healthCheck = async () => {
        const urls = API.getUrls();
        const checks = Object.keys(urls).map(key => ({
            key,
            url: `${urls[key]}/actuator/health`,
            el: `health-${key}`,
        }));

        for (const check of checks) {
            const el = document.getElementById(check.el);
            if (!el) continue;
            const statusEl = el.querySelector('.health-status');
            el.classList.remove('up', 'down');
            statusEl.textContent = '...';

            try {
                const res = await fetch(check.url, { signal: AbortSignal.timeout(5000) });
                if (res.ok) {
                    el.classList.add('up');
                    statusEl.textContent = '✅ UP';
                } else {
                    el.classList.add('down');
                    statusEl.textContent = `❌ ${res.status}`;
                }
            } catch {
                el.classList.add('down');
                statusEl.textContent = '❌ Offline';
            }
        }
    };

    const useGateway = () => {
        const gw = API.GATEWAY_URL;
        const urls = { os: gw, billing: gw, execution: gw, customer: gw, catalog: gw, people: gw };
        API.setUrls(urls);
        API.toast('Usando Gateway (porta 8080) para todos os serviços', 'success');
        navigateTo('settings');
    };

    const useDirect = () => {
        API.setUrls({ ...API.DIRECT_URLS });
        API.toast('Usando portas diretas de cada serviço', 'info');
        navigateTo('settings');
    };

    // ── Inicialização ──
    const init = () => {
        // Configurar navegação do sidebar
        document.querySelectorAll('.nav-item[data-panel]').forEach(btn => {
            btn.addEventListener('click', () => {
                navigateTo(btn.dataset.panel);
            });
        });

        // Console toggle
        const toggleBtn = document.getElementById('toggleConsole');
        if (toggleBtn) toggleBtn.addEventListener('click', API.toggleConsole);

        // Console clear
        const clearBtn = document.getElementById('consoleClearBtn');
        if (clearBtn) clearBtn.addEventListener('click', API.clearConsole);

        // Atualizar indicador de autenticação
        if (typeof AuthModule !== 'undefined') {
            AuthModule.updateAuthIndicator();
        }

        // Renderizar painel inicial (OS é o default)
        navigateTo('os');
    };

    document.addEventListener('DOMContentLoaded', init);

    return {
        navigateTo,
        saveSettings,
        resetSettings,
        healthCheck,
        useGateway,
        useDirect,
        // Exposição dos sub-módulos para chamadas inline
        OS: typeof OSModule !== 'undefined' ? OSModule : null,
        Billing: typeof BillingModule !== 'undefined' ? BillingModule : null,
        Execution: typeof ExecucaoModule !== 'undefined' ? ExecucaoModule : null,
        Saga: typeof SagaModule !== 'undefined' ? SagaModule : null,
    };
})();
