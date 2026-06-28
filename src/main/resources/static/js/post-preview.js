document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".feed-content-preview").forEach(function (preview) {
        const fade = preview.querySelector(".feed-content-fade");

        if (!fade) {
            return;
        }

        requestAnimationFrame(function () {
            const isOverflowing = preview.scrollHeight > preview.clientHeight + 8;

            if (!isOverflowing) {
                fade.remove();
            }
        });
    });
});