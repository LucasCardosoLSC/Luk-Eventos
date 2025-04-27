document.addEventListener('DOMContentLoaded', carregarEventos);

// Formulário de Cadastro
document.getElementById('eventoForm').addEventListener('submit', cadastrarEvento);

// Formulário de Edição
document.getElementById('formEdicao').addEventListener('submit', salvarEdicao);
document.getElementById('cancelarEdicao').addEventListener('click', cancelarEdicao);

let eventoEditandoId = null; // Armazena o ID do evento que está sendo editado

async function carregarEventos() {
    const response = await fetch('http://localhost:8080/eventos');
    const eventos = await response.json();

    const tbody = document.getElementById('eventosTable').querySelector('tbody');
    tbody.innerHTML = '';

    eventos.forEach(evento => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${evento.nome}</td>
            <td>${evento.descricao}</td>
            <td>${new Date(evento.dataHora).toLocaleString()}</td>
            <td>${evento.local}</td>
            <td>
                <button onclick="editarEvento(${evento.id})">Editar</button>
                <button onclick="deletarEvento(${evento.id})">Excluir</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

async function cadastrarEvento(e) {
    e.preventDefault();

    const evento = {
        nome: document.getElementById('nome').value,
        descricao: document.getElementById('descricao').value,
        dataHora: document.getElementById('dataHora').value,
        local: document.getElementById('local').value
    };

    await fetch('http://localhost:8080/eventos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(evento)
    });

    document.getElementById('mensagem').innerText = "Evento cadastrado com sucesso!";
    carregarEventos();
    document.getElementById('eventoForm').reset();
}

async function editarEvento(id) {
    const response = await fetch(`http://localhost:8080/eventos/${id}`);
    const evento = await response.json();

    // Preenche os campos do formulário de edição
    document.getElementById('editNome').value = evento.nome;
    document.getElementById('editDescricao').value = evento.descricao;
    document.getElementById('editDataHora').value = evento.dataHora;
    document.getElementById('editLocal').value = evento.local;

    // Mostra o formulário de edição e esconde o formulário de cadastro
    document.getElementById('formEdicaoContainer').style.display = 'block';
    document.getElementById('eventoForm').style.display = 'none';

    // Armazena o ID do evento que está sendo editado
    eventoEditandoId = id;
}

async function salvarEdicao(e) {
    e.preventDefault();

    const nome = document.getElementById('editNome').value.trim();
    const descricao = document.getElementById('editDescricao').value.trim();
    const dataHora = document.getElementById('editDataHora').value.trim();
    const local = document.getElementById('editLocal').value.trim();

    if (!nome || !descricao || !dataHora || !local) {
        document.getElementById('mensagem').innerText = "Todos os campos são obrigatórios.";
        return;
    }

    const eventoAtualizado = { nome, descricao, dataHora, local };

    try {
        const response = await fetch(`http://localhost:8080/eventos/${eventoEditandoId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(eventoAtualizado)
        });

        if (response.ok) {
            document.getElementById('mensagem').innerText = "Evento atualizado com sucesso!";
            carregarEventos();
            cancelarEdicao();
        } else {
            const errorMessage = await response.text();
            document.getElementById('mensagem').innerText = `Erro ao atualizar o evento: ${errorMessage}`;
        }
    } catch (error) {
        document.getElementById('mensagem').innerText = `Erro ao atualizar o evento: ${error.message}`;
    }
}

function cancelarEdicao() {
    // Esconde o formulário de edição e mostra o formulário de cadastro
    document.getElementById('formEdicaoContainer').style.display = 'none';
    document.getElementById('eventoForm').style.display = 'block';

    // Limpa os campos do formulário de edição
    document.getElementById('formEdicao').reset();

    // Reseta o ID do evento que está sendo editado
    eventoEditandoId = null;
}

async function deletarEvento(id) {
    await fetch(`http://localhost:8080/eventos/${id}`, {
        method: 'DELETE'
    });

    document.getElementById('mensagem').innerText = "Evento excluído com sucesso!";
    carregarEventos();
}