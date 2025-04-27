package com.lukeventos.models;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    private boolean ativo;

    @Column(name = "tipo_usuario")
    @Enumerated(EnumType.STRING)
    private TipoUsuario tipo;

    @Column(name = "carteirinha_url")
    private String carteirinhaUrl;

    @Column(name = "carteirinha_status")
    @Enumerated(EnumType.STRING)
    private StatusCarteirinha carteirinhaStatus;

    @Column(name = "texto_extraido_carteirinha")
    private String textoExtraidoCarteirinha;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuarios_perfis",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "perfil_id")
    )
    private Set<Perfil> perfis = new HashSet<>();

    @OneToOne(mappedBy = "usuario")
    private Atletica atletica;

    public enum TipoUsuario {
        ADMIN,
        ATLETICA,
        UNIVERSITARIO
    }

    public enum StatusCarteirinha {
        PENDENTE,
        APROVADA,
        REJEITADA
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        perfis.forEach(perfil -> authorities.add(new SimpleGrantedAuthority(perfil.getNome())));
        return authorities;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public String getCarteirinhaUrl() {
        return carteirinhaUrl;
    }

    public void setCarteirinhaUrl(String carteirinhaUrl) {
        this.carteirinhaUrl = carteirinhaUrl;
    }

    public StatusCarteirinha getCarteirinhaStatus() {
        return carteirinhaStatus;
    }

    public void setCarteirinhaStatus(StatusCarteirinha carteirinhaStatus) {
        this.carteirinhaStatus = carteirinhaStatus;
    }

    public String getTextoExtraidoCarteirinha() {
        return textoExtraidoCarteirinha;
    }

    public void setTextoExtraidoCarteirinha(String textoExtraidoCarteirinha) {
        this.textoExtraidoCarteirinha = textoExtraidoCarteirinha;
    }

    public Set<Perfil> getPerfis() {
        return perfis;
    }

    public void setPerfis(Set<Perfil> perfis) {
        this.perfis = perfis;
    }

    public Atletica getAtletica() {
        return atletica;
    }

    public void setAtletica(Atletica atletica) {
        this.atletica = atletica;
    }
} 