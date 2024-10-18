function sidebar_not_visible() {
  document.querySelector('#header').classList.toggle('not-visible');
  document.querySelector('.header').classList.toggle('active-header');
  document.querySelector('.middle').classList.toggle('center-menu');
  document.querySelectorAll('.text').forEach(t => t.classList.toggle('deActive'));
  document.querySelectorAll('.menu').forEach(m => m.classList.toggle('center-menu'));
  document.querySelector('.user-info').classList.toggle('deActive');
  document.querySelector('img[alt="logout-btn"]').classList.toggle('deActive');
  document.querySelector('.footer').classList.toggle('not-visible');
  document.querySelector('#header-controller').classList.toggle('header-visible');
}

function user_info_toggle () {
  const detail_menu = document.querySelector('.detail-menu');
  detail_menu.classList.toggle('hidden');
}