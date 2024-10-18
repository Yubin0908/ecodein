const currentPage = window.location.pathname.split('/', -1)[1]

let currentMenu = document.querySelector(`.menu-a[data-page="${currentPage}"]`);
if (currentPage == 'stock' || currentPage == 'item') {
    currentMenu = document.querySelector(`.menu-a[data-page="stock item"]`);
}

console.log(currentPage)

if (currentMenu) {

      currentMenu.classList.add('active-menu');

}