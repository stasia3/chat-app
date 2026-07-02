document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("likesModal");
    const modalBody = document.getElementById("likesModalBody");
    const closeBtn = document.getElementById("closeLikesModal");

    function createLikedUserItem(user) {
        const item = document.createElement("a");

        item.className = "liked-user-item";
        item.href = `/profile/${user.username}`;

        const avatarContent = user.profileImageUrl
            ? `<img src="${user.profileImageUrl}" alt="Profile photo">`
            : `<span>${user.username.charAt(0).toUpperCase()}</span>`;

        item.innerHTML = `
            <div class="liked-user-avatar">
                ${avatarContent}
            </div>

            <strong>${user.username}</strong>
        `;

        return item;
    }

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
                            modalBody.appendChild(createLikedUserItem(user));
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