
let tasks = [];
let assignments = JSON.parse(localStorage.getItem("assignments")) || [];
let studies = JSON.parse(localStorage.getItem("studies")) || [];

function saveData() {
    localStorage.setItem("tasks", JSON.stringify(tasks));
    localStorage.setItem("assignments", JSON.stringify(assignments));
    localStorage.setItem("studies", JSON.stringify(studies));
}

function updateDashboard() {
    document.getElementById("totalTasks").textContent = tasks.length;

    const completed = tasks.filter(task => task.completed).length;

    document.getElementById("completedTasks").textContent = completed;
    document.getElementById("pendingTasks").textContent =
        tasks.length - completed;
}

async function addTask() {
    const input = document.getElementById("taskInput");
    const name = input.value.trim();

    if (!name) {
        alert("Please enter a task!");
        return;
    }

    try {
        const response = await fetch("https://studymate-z4t0.onrender.com/api/tasks", {
            method: "POST",
            headers: {
                "Content-Type": "text/plain"
            },
            body: name
        });

        if (!response.ok) {
            throw new Error("Unable to add task");
        }

        input.value = "";
        await loadTasks();

    } catch (error) {
        alert("Could not connect to Java server!");
        console.error(error);
    }
}
async function loadTasks() {
    const response = await fetch("https://studymate-z4t0.onrender.com/api/tasks");

    tasks = await response.json();

    renderTasks();
}
async function completeTask(index) {
    try {
        const response = await fetch(
            `https://studymate-z4t0.onrender.com/api/tasks/${index}/complete`,
            { method: "PUT" }
        );

        if (!response.ok) throw new Error("Could not complete task");

        await loadTasks();

    } catch (error) {
        alert("Unable to complete task!");
        console.error(error);
    }
}
async function undoTask(index) {
    try {
        const response = await fetch(
            `https://studymate-z4t0.onrender.com/api/tasks/${index}/undo`,
            { method: "PUT" }
        );

        if (!response.ok) {
            throw new Error("Could not undo task");
        }

        await loadTasks();

    } catch (error) {
        alert("Unable to undo task!");
        console.error(error);
    }
}

async function deleteTask(index) {
    try {
        const response = await fetch(
            `https://studymate-z4t0.onrender.com/api/tasks/${index}`,
            { method: "DELETE" }
        );

        if (!response.ok) throw new Error("Could not delete task");

        await loadTasks();

    } catch (error) {
        alert("Unable to delete task!");
        console.error(error);
    }
}
function renderTasks() {
    const list = document.getElementById("taskList");
    list.innerHTML = "";

    tasks.forEach((task, index) => {
        const li = document.createElement("li");
        const span = document.createElement("span");

        span.textContent = task.name;
        if (task.completed) span.style.textDecoration = "line-through";

        const completeBtn = document.createElement("button");
        completeBtn.textContent = task.completed ? "Undo" : "Complete";
        completeBtn.onclick = () => {
            if (task.completed) {
                undoTask(index);
            } else {
                 completeTask(index);
            }
        };

        const deleteBtn = document.createElement("button");
        deleteBtn.textContent = "Delete";
        deleteBtn.onclick = () => deleteTask(index);

        li.append(span, completeBtn, deleteBtn);
        list.appendChild(li);
    });

    updateDashboard();
}

function addAssignment() {
    const input = document.getElementById("assignmentInput");
    const date = document.getElementById("assignmentDate");

    if (!input.value.trim() || !date.value)
        return alert("Enter assignment name and due date!");

    assignments.push({
        name: input.value.trim(),
        date: date.value
    });

    input.value = "";
    date.value = "";

    saveData();
    renderAssignments();
}

function renderAssignments() {
    const list = document.getElementById("assignmentList");
    list.innerHTML = "";

    assignments.forEach((item, index) => {
        const li = document.createElement("li");
        const span = document.createElement("span");

        span.textContent = item.name + " - Due: " + item.date;

        const btn = document.createElement("button");
        btn.textContent = "Delete";
        btn.onclick = () => {
            assignments.splice(index, 1);
            saveData();
            renderAssignments();
        };

        li.append(span, btn);
        list.appendChild(li);
    });
}

function addStudy() {
    const input = document.getElementById("studyInput");
    const name = input.value.trim();

    if (!name) return alert("Enter a study schedule!");

    studies.push(name);
    input.value = "";

    saveData();
    renderStudies();
}

function renderStudies() {
    const list = document.getElementById("studyList");
    list.innerHTML = "";

    studies.forEach((study, index) => {
        const li = document.createElement("li");
        const span = document.createElement("span");

        span.textContent = study;

        const btn = document.createElement("button");
        btn.textContent = "Delete";
        btn.onclick = () => {
            studies.splice(index, 1);
            saveData();
            renderStudies();
        };

        li.append(span, btn);
        list.appendChild(li);
    });
}

loadTasks();
renderAssignments();
renderStudies();
