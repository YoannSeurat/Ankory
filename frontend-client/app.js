/* Ankory - client web
   Le backend est servi sur la même origine, donc les chemins sont relatifs.
   Pour un serveur statique séparé (Live Server), mettre API_BASE à
   "http://localhost:8080" et ajouter @CrossOrigin sur les contrôleurs. */
const API_BASE = "";

const STATUSES = ["IN_PREPARATION", "EN_LIVRAISON", "LIVRE"];
const LABELS = {
  IN_PREPARATION: "En préparation",
  EN_LIVRAISON: "En livraison",
  LIVRE: "Livré"
};

const state = {
  restaurants: [],
  clientRestaurantId: null,
  staffRestaurantId: null,
  menu: [],
  cart: [],
  order: null,
  poll: null
};

const $ = (id) => document.getElementById(id);
const euro = (n) => Number(n).toFixed(2).replace(".", ",") + " €";
const reference = (id) => "CMD-" + String(id).padStart(3, "0");

async function api(path, options) {
  const res = await fetch(API_BASE + path, {
    headers: { "Content-Type": "application/json" },
    ...options
  });
  if (!res.ok) {
    const body = await res.json().catch(() => ({}));
    throw new Error(body.message || "Erreur " + res.status);
  }
  return res.status === 204 ? null : res.json();
}

let toastTimer = null;
function toast(message) {
  const el = $("toast");
  el.textContent = message;
  el.classList.remove("hidden");
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => el.classList.add("hidden"), 3500);
}

/* ---------- navigation ---------- */

document.querySelectorAll(".view-tab").forEach((tab) => {
  tab.addEventListener("click", () => {
    document.querySelectorAll(".view-tab").forEach((t) => t.classList.remove("is-active"));
    tab.classList.add("is-active");
    const staff = tab.dataset.view === "staff";
    $("view-client").classList.toggle("hidden", staff);
    $("view-staff").classList.toggle("hidden", !staff);
    if (staff) loadOrders();
  });
});

/* ---------- restaurants ---------- */

async function loadRestaurants() {
  state.restaurants = await api("/restaurants");
  if (!state.restaurants.length) {
    $("menu").innerHTML = '<p class="empty">Aucun restaurant en base.</p>';
    return;
  }
  state.clientRestaurantId = state.restaurants[0].id;
  state.staffRestaurantId = state.restaurants[0].id;
  renderClientChips();
  renderStaffChips();
  await loadMenu();
}

function renderChips(containerId, activeId, onPick) {
  const box = $(containerId);
  box.innerHTML = "";
  state.restaurants.forEach((r) => {
    const btn = document.createElement("button");
    btn.type = "button";
    btn.className = "chip" + (r.id === activeId ? " is-active" : "");
    btn.textContent = r.name;
    btn.addEventListener("click", () => onPick(r.id));
    box.appendChild(btn);
  });
}

function renderClientChips() {
  renderChips("restaurant-list", state.clientRestaurantId, (id) => {
    state.clientRestaurantId = id;
    state.cart = [];
    renderClientChips();
    loadMenu();
  });
}

function renderStaffChips() {
  renderChips("staff-restaurant-list", state.staffRestaurantId, (id) => {
    state.staffRestaurantId = id;
    renderStaffChips();
    loadOrders();
  });
}

/* ---------- menu ---------- */

async function loadMenu() {
  state.menu = await api("/restaurants/" + state.clientRestaurantId + "/menu");
  renderMenu();
  renderCart();
}

function renderMenu() {
  const box = $("menu");
  box.innerHTML = "";

  const categories = [];
  state.menu.forEach((item) => {
    if (!categories.includes(item.category)) categories.push(item.category);
  });

  categories.forEach((category) => {
    const label = document.createElement("p");
    label.className = "label";
    label.textContent = category;
    box.appendChild(label);

    const block = document.createElement("div");
    block.className = "block";

    state.menu
      .filter((item) => item.category === category)
      .forEach((item) => {
        const row = document.createElement("div");
        row.className = "row";
        row.innerHTML =
          '<div class="row-text">' +
          '<p class="row-name"></p>' +
          '<p class="row-desc"></p>' +
          "</div>" +
          '<span class="row-price"></span>' +
          '<button type="button" class="btn-add" aria-label="Ajouter">+</button>';
        row.querySelector(".row-name").textContent = item.name;
        row.querySelector(".row-desc").textContent = item.description;
        row.querySelector(".row-price").textContent = euro(item.price);
        row.querySelector(".btn-add").addEventListener("click", () => addToCart(item));
        block.appendChild(row);
      });

    box.appendChild(block);
  });
}

/* ---------- panier ---------- */

function addToCart(item) {
  const line = state.cart.find((l) => l.menuItemId === item.id);
  if (line) {
    line.quantity += 1;
  } else {
    state.cart.push({
      menuItemId: item.id,
      name: item.name,
      price: Number(item.price),
      quantity: 1
    });
  }
  renderCart();
}

function changeQuantity(menuItemId, delta) {
  const line = state.cart.find((l) => l.menuItemId === menuItemId);
  if (!line) return;
  line.quantity += delta;
  if (line.quantity <= 0) {
    state.cart = state.cart.filter((l) => l.menuItemId !== menuItemId);
  }
  renderCart();
}

function cartTotal() {
  return state.cart.reduce((sum, l) => sum + l.price * l.quantity, 0);
}

function renderCart() {
  const box = $("cart-lines");
  box.innerHTML = "";

  if (!state.cart.length) {
    box.innerHTML = '<p class="empty">Ajoutez un plat pour commencer.</p>';
  }

  state.cart.forEach((line) => {
    const row = document.createElement("div");
    row.className = "cart-line";
    row.innerHTML =
      '<span class="stepper">' +
      '<button type="button" aria-label="Retirer">-</button>' +
      "<span></span>" +
      '<button type="button" aria-label="Ajouter">+</button>' +
      "</span>" +
      '<span class="cart-name"></span>' +
      '<span class="cart-price"></span>';
    const buttons = row.querySelectorAll(".stepper button");
    row.querySelector(".stepper span").textContent = line.quantity;
    row.querySelector(".cart-name").textContent = line.name;
    row.querySelector(".cart-price").textContent = euro(line.price * line.quantity);
    buttons[0].addEventListener("click", () => changeQuantity(line.menuItemId, -1));
    buttons[1].addEventListener("click", () => changeQuantity(line.menuItemId, 1));
    box.appendChild(row);
  });

  $("cart-total").textContent = euro(cartTotal());
  $("submit-order").disabled = state.cart.length === 0;
}

$("submit-order").addEventListener("click", async () => {
  if (!state.cart.length) return;
  try {
    const order = await api("/orders", {
      method: "POST",
      body: JSON.stringify({
        restaurantId: state.clientRestaurantId,
        lines: state.cart.map((l) => ({ menuItemId: l.menuItemId, quantity: l.quantity }))
      })
    });
    state.order = order;
    state.cart = [];
    renderCart();
    showTracking();
  } catch (err) {
    toast(err.message);
  }
});

/* ---------- suivi ---------- */

function showTracking() {
  $("cart-panel").classList.add("hidden");
  $("track-panel").classList.remove("hidden");
  renderTracking();
  clearInterval(state.poll);
  state.poll = setInterval(refreshOrder, 5000);
}

async function refreshOrder() {
  if (!state.order) return;
  try {
    state.order = await api("/orders/" + state.order.id);
    renderTracking();
    if (state.order.status === "LIVRE") clearInterval(state.poll);
  } catch (err) {
    clearInterval(state.poll);
  }
}

function renderTracking() {
  const order = state.order;
  $("track-ref").textContent = reference(order.id);

  const badge = $("track-badge");
  badge.textContent = LABELS[order.status];
  badge.className = "badge " + badgeClass(order.status);

  const reached = STATUSES.indexOf(order.status);
  document.querySelectorAll(".step").forEach((step, index) => {
    step.classList.toggle("is-done", index <= reached);
  });

  const box = $("track-lines");
  box.innerHTML = "";
  order.lines.forEach((line) => {
    const row = document.createElement("div");
    row.className = "track-line";
    row.innerHTML = "<span></span><span></span>";
    const cells = row.querySelectorAll("span");
    cells[0].textContent = line.quantity + "x " + lineName(line);
    cells[1].textContent = euro(line.unitPrice * line.quantity);
    box.appendChild(row);
  });

  const total = document.createElement("div");
  total.className = "track-total";
  total.innerHTML = "<span>Total</span><span></span>";
  total.querySelectorAll("span")[1].textContent = euro(order.totalAmount);
  box.appendChild(total);
}

function lineName(line) {
  if (line.name) return line.name;
  const item = state.menu.find((i) => i.id === line.menuItemId);
  return item ? item.name : "Plat #" + line.menuItemId;
}

$("new-order").addEventListener("click", () => {
  clearInterval(state.poll);
  state.order = null;
  $("track-panel").classList.add("hidden");
  $("cart-panel").classList.remove("hidden");
  renderCart();
});

/* ---------- restaurateur ---------- */

function badgeClass(status) {
  if (status === "EN_LIVRAISON") return "badge-livraison";
  if (status === "LIVRE") return "badge-livre";
  return "badge-prep";
}

async function loadOrders() {
  if (!state.staffRestaurantId) return;
  const box = $("staff-orders");
  try {
    const orders = await api("/orders?restaurantId=" + state.staffRestaurantId);
    box.innerHTML = "";

    if (!orders.length) {
      box.innerHTML = '<p class="empty">Aucune commande pour le moment.</p>';
      return;
    }

    orders.forEach((order) => {
      const row = document.createElement("div");
      row.className = "order-row";
      row.innerHTML =
        '<div class="row-text">' +
        '<p class="order-ref"></p>' +
        '<p class="order-items"></p>' +
        "</div>" +
        '<span class="badge"></span>';

      row.querySelector(".order-ref").textContent = reference(order.id);
      row.querySelector(".order-items").textContent =
        order.lines.map((l) => l.quantity + "x " + lineName(l)).join(", ") +
        " — " +
        euro(order.totalAmount);

      const badge = row.querySelector(".badge");
      badge.textContent = LABELS[order.status];
      badge.className = "badge " + badgeClass(order.status);

      if (order.status !== "LIVRE") {
        const button = document.createElement("button");
        button.type = "button";
        button.className = "btn-next";
        button.textContent = "Étape suivante";
        button.addEventListener("click", () => advance(order));
        row.appendChild(button);
      }

      box.appendChild(row);
    });
  } catch (err) {
    box.innerHTML = '<p class="empty"></p>';
    box.querySelector(".empty").textContent = err.message;
  }
}

async function advance(order) {
  const next = STATUSES[STATUSES.indexOf(order.status) + 1];
  if (!next) return;
  try {
    await api("/orders/" + order.id + "/status", {
      method: "PATCH",
      body: JSON.stringify({ status: next })
    });
    loadOrders();
  } catch (err) {
    toast(err.message);
  }
}

/* ---------- démarrage ---------- */

loadRestaurants().catch((err) => toast(err.message));
