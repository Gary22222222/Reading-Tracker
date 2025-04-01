document.addEventListener('DOMContentLoaded', () => {
    const logForm = document.getElementById('logForm');
    const logsContainer = document.getElementById('logs');

    // Fetch and display logs
    function fetchLogs() {
        fetch('/api/reading-logs', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}` // 如果需要认证
            }
        })
            .then(handleResponse)
            .then(logs => {
                logsContainer.innerHTML = '';
                logs.forEach(log => {
                    const logEntry = document.createElement('div');
                    logEntry.className = 'log-entry';
                    logEntry.innerHTML = `
                    <h3>${log.title}</h3>
                    <p>Author: ${log.author}</p>
                    <p>Date: ${log.date}</p>
                    <p>Time Spent: ${log.timeSpent} minutes</p>
                    <p>Notes: ${log.notes}</p>
                    <button onclick="deleteLog(${log.id})">Delete</button>
                `;
                    logsContainer.appendChild(logEntry);
                });
            })
            .catch(error => {
                console.error(error);
                logsContainer.innerHTML = '<p>Error fetching logs</p>';
            });
    }

    // Add new log
    logForm.addEventListener('submit', event => {
        event.preventDefault();

        const logData = {
            title: document.getElementById('title').value,
            author: document.getElementById('author').value,
            date: document.getElementById('date').value,
            timeSpent: document.getElementById('timeSpent').value,
            notes: document.getElementById('notes').value,
        };

        fetch('/api/reading-logs', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('token')}` // 如果需要认证
            },
            body: JSON.stringify(logData),
        })
            .then(handleResponse)
            .then(() => {
                fetchLogs();
                logForm.reset();
            })
            .catch(error => {
                console.error(error);
                alert(error.message);
            });
    });

    // Delete log
    window.deleteLog = function(logId) {
        fetch(`/api/reading-logs/${logId}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}` // 如果需要认证
            }
        })
            .then(handleResponse)
            .then(() => fetchLogs())
            .catch(error => {
                console.error(error);
                alert(error.message);
            });
    };

    // Handle response (including non-JSON responses)
    function handleResponse(response) {
        if (!response.ok) {
            const contentType = response.headers.get('Content-Type');
            if (contentType && contentType.includes('application/json')) {
                return response.json().then(error => {
                    throw new Error(error.error || 'Unknown error');
                });
            } else {
                throw new Error(`Unexpected response: ${response.statusText}`);
            }
        }
        return response.json();
    }

    // Initial fetch
    fetchLogs();
});
