document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("likesModal");
    const modalBody = document.getElementById("likesModalBody");
    const closeBtn = document.getElementById("closeLikesModal");

    document.querySelectorAll(".likes-list-btn").forEach(function (button) {
        button.addEventListener("click", function () {
            fetch(button.dataset.url)
                .then(response => response.json())
                .then(users => {
                    modalBody.innerHTML = "";

                    if (users.length === 0) {
                        modalBody.innerHTML = `<p class="likes-empty">No likes yet.</p>`;
                    } else {
                        users.forEach(user => {
                            const item = document.createElement("div");
                            item.className = "liked-user-item";

                            item.innerHTML = `
                                <div class="liked-user-avatar">
                                    ${user.username.charAt(0).toUpperCase()}
                                </div>
                                <strong>${user.username}</strong>
                            `;

                            modalBody.appendChild(item);
                        });
                    }

                    modal.classList.remove("hidden");
                });
        });
    });

    closeBtn.addEventListener("click", function () {
        modal.classList.add("hidden");
    });

    modal.addEventListener("click", function (event) {
        if (event.target === modal) {
            modal.classList.add("hidden");
        }
    });
});