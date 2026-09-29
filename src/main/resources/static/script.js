const API = "http://localhost:8080/api";

/* ================================================= */
/* GENERAL API HELPER */
/* ================================================= */

async function apiRequest(url, options = {}) {
    try {
        const response = await fetch(API + url, {
            headers: {
                "Content-Type": "application/json",
                ...(options.headers || {})
            },
            ...options
        });

        const text = await response.text();

        let data = null;

        try {
            data = text ? JSON.parse(text) : null;
        } catch {
            data = text;
        }

        if (!response.ok) {
            let message = "Request failed";

            if (data) {
                if (typeof data === "string") {
                    message = data;
                } else if (data.message) {
                    message = data.message;
                } else if (data.error) {
                    message = data.error;
                }
            }

            throw new Error(`${response.status}: ${message}`);
        }

        return data;
    } catch (error) {
        console.error(error);
        throw error;
    }
}

/* ================================================= */
/* MESSAGE */
/* ================================================= */

function showMessage(elementId, message, success = true) {
    const element = document.getElementById(elementId);

    if (!element) return;

    element.textContent = message;
    element.className = success ? "message success" : "message error";
}

/* ================================================= */
/* CREATE STUDENT */
/* ================================================= */

document
    .getElementById("studentForm")
    .addEventListener("submit", async function (event) {
        event.preventDefault();

        const name = document.getElementById("studentName").value.trim();
        const email = document.getElementById("studentEmail").value.trim();
        const passwordElement = document.getElementById("studentPassword");
        const password = passwordElement ? passwordElement.value : "";

        try {
            const student = await apiRequest("/students", {
                method: "POST",
                body: JSON.stringify({
                    name: name,
                    email: email,
                    password: password
                })
            });

            showMessage(
                "studentMessage",
                `Student created successfully! ID: ${student.id}`,
                true
            );

            document.getElementById("studentForm").reset();
            loadStudents();
        } catch (error) {
            showMessage("studentMessage", error.message, false);
        }
    });

/* ================================================= */
/* LOAD STUDENTS */
/* ================================================= */

async function loadStudents() {
    const container = document.getElementById("studentsContainer");

    if (!container) return;

    try {
        const students = await apiRequest("/students");

        if (!students || students.length === 0) {
            container.innerHTML = `<p class="muted">No students found.</p>`;
            return;
        }

        container.innerHTML = students
            .map(
                (student) => `
                    <div class="student-item">
                        <div class="student-info">
                            <strong>${escapeHtml(student.name)}</strong>
                            <span>${escapeHtml(student.email)}</span>
                        </div>
                        <div class="student-id">ID ${student.id}</div>
                    </div>
                `
            )
            .join("");
    } catch (error) {
        container.innerHTML = `<p class="muted">Unable to load students.</p>`;
    }
}

/* ================================================= */
/* ADD SUBJECT */
/* ================================================= */

const subjectForm = document.getElementById("subjectForm");

if (subjectForm) {
    subjectForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const name = document.getElementById("subjectName").value.trim();

        if (!name) {
            showMessage("subjectMessage", "Please enter a subject name.", false);
            return;
        }

        try {
            const subject = await apiRequest("/subjects", {
                method: "POST",
                body: JSON.stringify({
                    name: name
                })
            });

            showMessage(
                "subjectMessage",
                `Subject "${subject.name}" added successfully! ID: ${subject.id}`,
                true
            );

            subjectForm.reset();
            loadSubjects();
        } catch (error) {
            showMessage("subjectMessage", error.message, false);
        }
    });
}

/* ================================================= */
/* LOAD SUBJECTS */
/* ================================================= */

async function loadSubjects() {
    const container = document.getElementById("subjectsContainer");

    if (!container) return;

    try {
        const subjects = await apiRequest("/subjects");

        if (!subjects || subjects.length === 0) {
            container.innerHTML = `<p class="muted">No subjects found.</p>`;
            return;
        }

        container.innerHTML = subjects
            .map(
                (subject) => `
                    <div class="student-item">
                        <div class="student-info">
                            <strong>${escapeHtml(subject.name)}</strong>
                            <span>Subject ID: ${subject.id}</span>
                        </div>
                    </div>
                `
            )
            .join("");
    } catch (error) {
        container.innerHTML = `<p class="muted">Unable to load subjects.</p>`;
    }
}

/* ================================================= */
/* FIND SUBJECT BY NAME */
/* ================================================= */

async function findSubjectByName(name) {
    if (!name || !name.trim()) {
        throw new Error("Please enter a subject name.");
    }

    return await apiRequest(`/subjects/name/${encodeURIComponent(name.trim())}`);
}

/* ================================================= */
/* CREATE GROUP */
/* ================================================= */

document
    .getElementById("groupForm")
    .addEventListener("submit", async function (event) {
        event.preventDefault();

        const subjectName = document.getElementById("groupSubjectName").value.trim();
        const creatorId = Number(document.getElementById("groupCreatorId").value);
        const maxMembers = Number(document.getElementById("groupMaxMembers").value);

        if (!subjectName) {
            showMessage("groupMessage", "Enter a subject name.", false);
            return;
        }

        try {
            const subject = await findSubjectByName(subjectName);

            const group = await apiRequest("/groups", {
                method: "POST",
                body: JSON.stringify({
                    subjectId: subject.id,
                    creatorId: creatorId,
                    maxMembers: maxMembers
                })
            });

            showMessage(
                "groupMessage",
                `Group created successfully! Group ID: ${group.id}`,
                true
            );

            const searchInput = document.getElementById("searchSubjectName");
            if (searchInput) {
                searchInput.value = subject.name;
            }

            document.getElementById("groupForm").reset();

            if (searchInput) {
                searchInput.value = subject.name;
            }

            loadGroups();
        } catch (error) {
            showMessage("groupMessage", error.message, false);
        }
    });

/* ================================================= */
/* LOAD GROUPS BY SUBJECT NAME */
/* ================================================= */

async function loadGroups() {
    const input = document.getElementById("searchSubjectName");

    if (!input) return;

    const subjectName = input.value.trim();

    if (!subjectName) {
        showMessage("groupSearchMessage", "Please enter a subject name.", false);
        return;
    }

    const container = document.getElementById("groupsContainer");

    if (!container) return;

    container.innerHTML = `
        <div class="empty">
            <h3>Loading groups...</h3>
        </div>
    `;

    try {
        const subject = await findSubjectByName(subjectName);
        const groups = await apiRequest(`/groups/subject/${subject.id}`);

        if (!groups || groups.length === 0) {
            container.innerHTML = `
                <div class="empty">
                    <div class="empty-icon">✦</div>
                    <h3>No study groups</h3>
                    <p>No groups exist for "${escapeHtml(subject.name)}" yet.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = groups
            .map((group) => {
                const current = group.currentMemberCount || 0;
                const max = group.maxMembers || 1;
                const percentage = Math.min((current / max) * 100, 100);

                return `
                    <div class="group-card">
                        <div class="group-top">
                            <div class="group-icon">📚</div>
                            <span class="group-id">GROUP #${group.id}</span>
                        </div>
                        <h3>${escapeHtml(group.subject)}</h3>
                        <p>Focused study circle</p>
                        <div class="capacity">
                            <div class="capacity-bar" style="width:${percentage}%"></div>
                        </div>
                        <div class="capacity-text">
                            <span>${current} members</span>
                            <span>${max} max</span>
                        </div>
                    </div>
                `;
            })
            .join("");
    } catch (error) {
        container.innerHTML = `
            <div class="empty">
                <div class="empty-icon">⚠</div>
                <h3>Unable to load groups</h3>
                <p>${escapeHtml(error.message)}</p>
            </div>
        `;
    }
}

/* ================================================= */
/* JOIN GROUP */
/* ================================================= */

async function joinGroup() {
    const groupId = document.getElementById("joinGroupId").value;
    const studentId = document.getElementById("joinStudentId").value;

    if (!groupId || !studentId) {
        showMessage("joinMessage", "Enter Group ID and Student ID.", false);
        return;
    }

    try {
        const group = await apiRequest(`/groups/${groupId}/join/${studentId}`, {
            method: "POST"
        });

        showMessage(
            "joinMessage",
            `Student ${studentId} joined Group ${group.id} successfully.`,
            true
        );

        loadGroups();
    } catch (error) {
        showMessage("joinMessage", error.message, false);
    }
}

/* ================================================= */
/* LEAVE GROUP */
/* ================================================= */

async function leaveGroup() {
    const groupId = document.getElementById("leaveGroupId").value;
    const studentId = document.getElementById("leaveStudentId").value;

    if (!groupId || !studentId) {
        showMessage("leaveMessage", "Enter Group ID and Student ID.", false);
        return;
    }

    try {
        await apiRequest(`/groups/${groupId}/leave/${studentId}`, {
            method: "DELETE"
        });

        showMessage(
            "leaveMessage",
            `Student ${studentId} left Group ${groupId} successfully.`,
            true
        );

        loadGroups();
    } catch (error) {
        showMessage("leaveMessage", error.message, false);
    }
}

/* ================================================= */
/* REMOVE MEMBER */
/* ================================================= */

async function removeMember() {
    const groupId = document.getElementById("removeGroupId").value;
    const memberId = document.getElementById("removeMemberId").value;
    const creatorId = document.getElementById("removeCreatorId").value;

    if (!groupId || !memberId || !creatorId) {
        showMessage("removeMessage", "Enter Group ID, Member ID and Creator ID.", false);
        return;
    }

    try {
        await apiRequest(`/groups/${groupId}/members/${memberId}?creatorId=${creatorId}`, {
            method: "DELETE"
        });

        showMessage(
            "removeMessage",
            `Student ${memberId} removed successfully.`,
            true
        );

        loadGroups();
    } catch (error) {
        showMessage("removeMessage", error.message, false);
    }
}

/* ================================================= */
/* REFRESH EVERYTHING */
/* ================================================= */

function refreshAll() {
    loadStudents();
    loadSubjects();

    const searchInput = document.getElementById("searchSubjectName");

    if (searchInput && searchInput.value.trim()) {
        loadGroups();
    }
}

/* ================================================= */
/* HTML SAFETY */
/* ================================================= */

function escapeHtml(value) {
    if (value === null || value === undefined) {
        return "";
    }

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

/* ================================================= */
/* INITIAL LOAD */
/* ================================================= */

window.addEventListener("DOMContentLoaded", function () {
    loadStudents();
    loadSubjects();
});
