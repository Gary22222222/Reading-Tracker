document.addEventListener('DOMContentLoaded', () => {
    // 检查CSRF token是否存在，如果没有则重定向到登录页
    if (!document.querySelector('meta[name="_csrf"]')) {
        window.location.href = '/auth';
        return;
    }

    // DOM元素
    const logsContainer = document.getElementById('logs');
    const logForm = document.getElementById('logForm');
    const submitBtn = document.getElementById('submitBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const formTitle = document.getElementById('formTitle');
    const searchInput = document.getElementById('search');
    const searchBtn = document.getElementById('searchBtn');
    const sortSelect = document.getElementById('sort');
    const logoutBtn = document.getElementById('logoutBtn');

    // 状态变量
    let logs = [];
    let isEditing = false;
    let currentLogId = null;
    let searchTerm = '';
    let sortOption = 'date-desc';

    // 初始化应用
    function init() {
        fetchLogs();
        setupEventListeners();
    }

    // 设置事件监听器
    function setupEventListeners() {
        // 表单提交
        logForm.addEventListener('submit', handleFormSubmit);

        // 取消按钮
        cancelBtn.addEventListener('click', resetForm);

        // 搜索
        searchBtn.addEventListener('click', () => {
            searchTerm = searchInput.value.toLowerCase();
            renderLogs();
        });

        searchInput.addEventListener('keyup', (e) => {
            if (e.key === 'Enter') {
                searchTerm = searchInput.value.toLowerCase();
                renderLogs();
            }
        });

        // 排序
        sortSelect.addEventListener('change', () => {
            sortOption = sortSelect.value;
            renderLogs();
        });

        // 登出
        logoutBtn.addEventListener('click', handleLogout);
    }

    // 认证请求封装
    function makeAuthenticatedRequest(url, options = {}) {
        options.credentials = 'include';

        // 添加CSRF token
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

        options.headers = {
            ...options.headers,
            [csrfHeader]: csrfToken
        };

        return fetch(url, options)
            .then(response => {
                if (response.status === 401) {
                    window.location.href = '/auth';
                    return Promise.reject('Unauthorized');
                }
                return response;
            });
    }

    // 获取所有日志
    function fetchLogs() {
        logsContainer.innerHTML = '<div class="loading">Loading logs...</div>';

        makeAuthenticatedRequest('/api/reading-logs')
            .then(handleResponse)
            .then(data => {
                logs = data;
                renderLogs();
            })
            .catch(error => {
                console.error('Error fetching logs:', error);
                logsContainer.innerHTML = `<div class="empty-state">Error loading logs: ${error}</div>`;
            });
    }

    // 渲染日志列表
    function renderLogs() {
        if (logs.length === 0) {
            logsContainer.innerHTML = '<div class="empty-state">No reading logs found. Add your first log!</div>';
            return;
        }

        // 过滤
        let filteredLogs = logs.filter(log => {
            return log.title.toLowerCase().includes(searchTerm) ||
                log.author.toLowerCase().includes(searchTerm);
        });

        // 排序
        filteredLogs = sortLogs(filteredLogs, sortOption);

        // 渲染
        logsContainer.innerHTML = '';
        filteredLogs.forEach(log => {
            const logElement = createLogElement(log);
            logsContainer.appendChild(logElement);
        });
    }

    // 创建日志元素
    // app.js
    function createLogElement(log) {
        const logElement = document.createElement('div');
        logElement.className = 'log-entry';

        logElement.innerHTML = `
    <div class="log-header">
        <h3 class="log-title">${log.title}</h3>
        <div class="actions">
            <button class="view-btn" data-id="${log.id}">详情</button>
            <button class="edit-btn" data-id="${log.id}">编辑</button>
            <button class="delete-btn" data-id="${log.id}">删除</button>
        </div>
    </div>
    `;

        // 添加事件监听（保持原有功能）
        logElement.querySelector('.view-btn').addEventListener('click', () => viewDetails(log.id));
        logElement.querySelector('.edit-btn').addEventListener('click', () => editLog(log.id));
        logElement.querySelector('.delete-btn').addEventListener('click', () => deleteLog(log.id));

        return logElement;
    }

    // 新增查看详情功能
    // 在 app.js 中找到查看详情的函数
    function viewDetails(logId) {
        makeAuthenticatedRequest(`/api/reading-logs/${logId}`)
            .then(handleResponse)
            .then(log => {
                // 填充数据到详情弹窗
                document.getElementById("detailNotes").textContent = log.notes;
                // 显示弹窗
                document.getElementById("detailModal").style.display = "flex";
            })
            .catch(error => {
                alert('获取详情失败: ' + error.message);
            });
    }

// 关闭弹窗逻辑（确保关闭按钮绑定事件）
    document.querySelector("#detailModal .modal-close").addEventListener("click", () => {
        document.getElementById("detailModal").style.display = "none";
    });

// 显示详情弹窗
    // app.js
    function showDetailModal(log) {
        const modalHtml = `
    <div class="modal">
        <div class="modal-content">
            <h3>${log.title}</h3>
            <div class="modal-body">
                <p><strong>作者：</strong>${log.author}</p>
                <p><strong>日期：</strong>${new Date(log.date).toLocaleDateString()}</p>
                <p><strong>阅读时长：</strong>${log.timeSpent} 分钟</p>
                <p><strong>笔记：</strong>${log.notes || '无'}</p>
            </div>
            <button class="modal-close">关闭</button>
        </div>
    </div>
    `;

        const modalContainer = document.createElement('div');
        modalContainer.innerHTML = modalHtml;
        document.body.appendChild(modalContainer);

        // 关闭按钮点击事件
        modalContainer.querySelector('.modal-close').addEventListener('click', () => {
            document.body.removeChild(modalContainer);
        });
    }

    // 排序日志
    function sortLogs(logs, option) {
        const [field, direction] = option.split('-');

        return [...logs].sort((a, b) => {
            let comparison = 0;

            switch (field) {
                case 'date':
                    comparison = new Date(a.date) - new Date(b.date);
                    break;
                case 'time':
                    comparison = a.timeSpent - b.timeSpent;
                    break;
                case 'title':
                    comparison = a.title.localeCompare(b.title);
                    break;
                default:
                    comparison = 0;
            }

            return direction === 'desc' ? -comparison : comparison;
        });
    }

    // 处理表单提交
    function handleFormSubmit(e) {
        e.preventDefault();

        const logData = {
            title: document.getElementById('title').value,
            author: document.getElementById('author').value,
            date: document.getElementById('date').value,
            timeSpent: parseInt(document.getElementById('timeSpent').value),
            notes: document.getElementById('notes').value
        };

        if (isEditing) {
            updateLog(currentLogId, logData);
        } else {
            createLog(logData);
        }
    }

    // 创建新日志
    function createLog(logData) {
        makeAuthenticatedRequest('/api/reading-logs', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(logData)
        })
            .then(handleResponse)
            .then(() => {
                resetForm();
                fetchLogs();
            })
            .catch(error => {
                console.error('Error creating log:', error);
                alert(`Error: ${error.message}`);
            });
    }

    // 更新日志
    function updateLog(logId, logData) {
        makeAuthenticatedRequest(`/api/reading-logs/${logId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(logData)
        })
            .then(handleResponse)
            .then(() => {
                resetForm();
                fetchLogs();
            })
            .catch(error => {
                console.error('Error updating log:', error);
                alert(`Error: ${error.message}`);
            });
    }

    // 删除日志
    function deleteLog(logId) {
        if (!confirm('Are you sure you want to delete this reading log?')) return;

        makeAuthenticatedRequest(`/api/reading-logs/${logId}`, {
            method: 'DELETE'
        })
            .then(handleResponse)
            .then(() => {
                fetchLogs();
            })
            .catch(error => {
                console.error('Error deleting log:', error);
                alert(`Error: ${error.message}`);
            });
    }

    // 编辑日志
    function editLog(logId) {
        const log = logs.find(l => l.id === logId);
        if (!log) return;

        isEditing = true;
        currentLogId = logId;

        // 填充表单
        document.getElementById('logId').value = logId;
        document.getElementById('title').value = log.title;
        document.getElementById('author').value = log.author;
        document.getElementById('date').value = log.date;
        document.getElementById('timeSpent').value = log.timeSpent;
        document.getElementById('notes').value = log.notes || '';

        // 更新UI
        formTitle.textContent = 'Edit Reading Log';
        submitBtn.textContent = 'Update Log';
        cancelBtn.style.display = 'inline-block';

        // 滚动到表单
        document.querySelector('.form-container').scrollIntoView({ behavior: 'smooth' });
    }

    // 处理登出
    function handleLogout() {
        makeAuthenticatedRequest('/logout', {
            method: 'POST'
        })
            .then(() => {
                window.location.href = '/auth';
            })
            .catch(error => {
                console.error('Logout error:', error);
            });
    }

    // 重置表单
    function resetForm() {
        logForm.reset();
        isEditing = false;
        currentLogId = null;
        formTitle.textContent = 'Add New Reading Log';
        submitBtn.textContent = 'Add Log';

    }

    // 处理API响应
    function handleResponse(response) {
        if (!response.ok) {
            return response.json().then(error => {
                throw new Error(error.error || 'Unknown error occurred');
            });
        }
        return response.json();
    }

    // 初始化弹窗元素
    const formModal = document.getElementById("formModal");

// 点击"Add Log"按钮显示弹窗
    document.getElementById("addLogBtn").addEventListener("click", () => {
        resetForm();
        formModal.style.display = "flex";
    });

// 表单提交处理（新增关闭弹窗逻辑）
    function handleFormSubmit(e) {
        e.preventDefault();
        const logData = {
            title: document.getElementById("title").value,
            author: document.getElementById("author").value,
            date: document.getElementById("date").value,
            timeSpent: parseInt(document.getElementById("timeSpent").value),
            notes: document.getElementById("notes").value
        };

        if (isEditing) {
            updateLog(currentLogId, logData);
        } else {
            createLog(logData);
        }

        formModal.style.display = "none"; // 提交后关闭弹窗
    }

// 修改编辑函数以填充数据到弹窗
    function editLog(logId) {
        const log = logs.find(l => l.id === logId);
        if (!log) return;

        isEditing = true;
        currentLogId = logId;

        // 填充表单数据
        document.getElementById("title").value = log.title;
        document.getElementById("author").value = log.author;
        document.getElementById("date").value = log.date;
        document.getElementById("timeSpent").value = log.timeSpent;
        document.getElementById("notes").value = log.notes || "";

        // 更新弹窗标题和按钮文字
        document.getElementById("formTitle").textContent = "Edit Reading Log";
        document.getElementById("submitBtn").textContent = "Update Log";

        // 显示弹窗
        formModal.style.display = "flex";
    }

// 确保取消按钮点击关闭弹窗
    document.getElementById("cancelBtn").addEventListener("click", () => {
        document.getElementById("formModal").style.display = "none";
        resetForm(); // 重置表单状态
    });



    // 初始化应用
    init();
});