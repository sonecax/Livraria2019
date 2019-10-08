/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controle;

import dao.EditoraDAO;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.FormataData;
import modelo.Editora;

/**
 *
 * @author dappo
 */
@WebServlet(name = "EditoraWS", urlPatterns = {"/admin/editora/EditoraWS"})
public class EditoraWS extends HttpServlet {
       
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String acao = request.getParameter("txtAcao");
        RequestDispatcher destino;
        String pagina;
        EditoraDAO dao = new EditoraDAO();
        Editora obj;
        List<Editora> editoras;
        Boolean deucerto;
        String msg;
        
        switch(String.valueOf(acao)){
            case "add":
                //Abrir tela e talvez buscar dados
                pagina = "add.jsp";
                break;
            case "edit":
                //Abrir tela e buscar dados
                obj = dao.buscarPorChavePrimaria(Long.parseLong(request.getParameter("txtId")));
                request.setAttribute("obj", obj);
                pagina = "edit.jsp";
                break;
            case "del":
                //excluir dados e buscar dados
                obj = dao.buscarPorChavePrimaria(Long.parseLong(request.getParameter("txtId")));
                deucerto = dao.excluir(obj);
                if(deucerto){
                    msg = obj.getNome() + " deletado com sucesso!";
                }else{
                    msg = "Problema ao excluir o genero " + obj.getNome();
                }
                editoras = dao.listar();
                request.setAttribute("msg", msg);
                request.setAttribute("lista", editoras);
                pagina = "list.jsp";
                break;
            default:
                //listar ou listar com filtro
                String filtro = request.getParameter("txtFiltro");
                if(filtro == null){
                    //lista todos
                    editoras = dao.listar();
                }else{
                    //lista com filtro
                    try {                
                        editoras = dao.listar(filtro);
                    } catch (Exception ex) {
                        editoras = dao.listar();
                        msg = "Problema ao filtrar";
                        request.setAttribute("msg", msg);
                    }
                }
                request.setAttribute("lista", editoras);
                pagina = "list.jsp";
                break;
        }
        
        destino = request.getRequestDispatcher(pagina);
        destino.forward(request, response);
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //criar variaveis
        Editora obj;
        EditoraDAO dao = new EditoraDAO();
        Boolean deucerto;
        String msg;
        String pagina;
        RequestDispatcher destino;
        List<Editora> editoras;
        
        
         //Receber dados
        String id = request.getParameter("txtId");
        String nome = request.getParameter("txtNome");
        String endereco = request.getParameter("txtEndereco");
        String telefone = request.getParameter("txtTelefone");
        String data = request.getParameter("txtData");
        String logo = request.getParameter("txtLogo");
        
        
        //Tratar os dados (transformar os dados no formato solicitado)
        Date fundacao = FormataData.formata(data, "yyyy-MM-dd");
        
        
        if(id != null){
            //busca o que existe
           obj = dao.buscarPorChavePrimaria(Long.parseLong(id));
        }else{
            //cria um novo
           obj = new Editora(); 
        }
        
        //adicionar os dados recebidos
        obj.setNome(nome);
        obj.setEndereco(endereco);
        obj.setTelefone(telefone);
        obj.setFundacao(fundacao);
        obj.setLogo(logo);
        
        if(id != null){
            deucerto = dao.alterar(obj);
            pagina = "list.jsp";
            editoras = dao.listar();
            request.setAttribute("lista", editoras);
            if(deucerto){
                msg = obj.getNome() + " alterado com sucesso!";
            }else{
                msg = "Problema ao editar o genero " + obj.getNome();
            }
        }else{
            deucerto = dao.incluir(obj);
            pagina = "add.jsp";
            if(deucerto){
                msg = obj.getNome() + " adicionado com sucesso!";
            }else{
                msg = "Problema ao adicionar o genero " + obj.getNome();
            }
        }
        request.setAttribute("msg", msg);
        destino = request.getRequestDispatcher(pagina);
        destino.forward(request, response);
        
        
    }

}
