let laBotonera = document.getElementById("botonera");

const numbers = [...Array(10)].map((_,i) => i + 1)

numbers.forEach((numero) => {
    const boton = document.createElement("button");
    boton.innerHTML = numero
    laBotonera.appendChild(boton);
})

