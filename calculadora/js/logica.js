let laBotonera = document.getElementById("botonera");
let elVisor = document.getElementById("visor");
let calculado = document.getElementById("calcular");
let borrado = document.getElementById("AC");

const botonColor = (evt) => {
    evt.target.style.backgroundColor = "red";
}
const botonColorVuelta = (evt) => {
    evt.target.style.backgroundColor = "#000"; 
}
const copiarNumeroAVisor = (evt) => {
    if (elVisor.innerHTML == "Error") {
        borrarTodo(); 
    }
    elVisor.innerHTML += evt.target.innerHTML;
}
const procesarTecla = (evt) => {
    let codigo = evt.key;
    if (elVisor.innerHTML == "Error") {
        borrarTodo();
    }
    // Validar si es un número
    if (/[0-9]/.test(codigo)) {
        elVisor.innerHTML += codigo;
    }
    // Validar si es un operador permitido
    else if (["+", "-", "*", "/","x"].includes(codigo)) {
        if(codigo == "x"){
            codigo = "*";
        }
        elVisor.innerHTML += codigo;
    }
};

const hacerCuenta = () => {
    try {
        let resultado = eval(elVisor.innerHTML);
        elVisor.innerHTML = resultado;
    } catch (error) {
        elVisor.innerHTML = "Error";
    }
};
const borrarTodo = () => {
    elVisor.innerHTML = '';
}
[...Array(10)].forEach((_, i) => {
    const boton = document.createElement("button");
    boton.innerHTML = i;
    boton.onmouseleave = botonColorVuelta;
    boton.onmouseover = botonColor;
    boton.onclick = copiarNumeroAVisor;
    laBotonera.appendChild(boton);
});
const operaciones = ['+', '-', '*', '/'];
operaciones.forEach(op => {
    const boton = document.createElement("button");
    boton.innerHTML = op;
    boton.onmouseleave = botonColorVuelta;
    boton.onmouseover = botonColor;
    boton.onclick = copiarNumeroAVisor;
    laBotonera.appendChild(boton);
});

document.getElementById("AC").onclick = borrarTodo;
document.onkeyup = procesarTecla;
calculado.onclick = hacerCuenta;
